package org.example.expense_tracker.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // <-- Make sure this is imported
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*; // <-- NEW IMPORT
import javafx.scene.layout.BorderPane;
import org.example.expense_tracker.model.Transaction;
import org.example.expense_tracker.model.TransactionType; // <-- NEW IMPORT
import org.example.expense_tracker.model.User;
import org.example.expense_tracker.repository.TransactionRepository;
import org.example.expense_tracker.service.UserSession;
import org.example.expense_tracker.service.ViewSwitcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
public class MainViewController {

    @FXML
    private BorderPane mainBorderPane;
    private Node transactionHistoryView;
    private final ApplicationContext applicationContext;

    @FXML
    private Label welcomeLabel;

    @FXML
    private MenuItem logoutMenuItem;

    @FXML
    private TableView<Transaction> transactionsTable;

    @FXML
    private TableColumn<Transaction, LocalDate> dateColumn;

    @FXML
    private TableColumn<Transaction, String> descriptionColumn;

    @FXML
    private TableColumn<Transaction, BigDecimal> amountColumn;

    @FXML
    private TableColumn<Transaction, String> categoryColumn;

    private final UserSession userSession;
    private final ViewSwitcher viewSwitcher;
    private final TransactionRepository transactionRepository;

    @Autowired
    public MainViewController(UserSession userSession, ViewSwitcher viewSwitcher,
                              TransactionRepository transactionRepository, ApplicationContext applicationContext) {
        this.userSession = userSession;
        this.viewSwitcher = viewSwitcher;
        this.transactionRepository = transactionRepository;
        this.applicationContext = applicationContext;
    }

    @FXML
    public void initialize() {
        User user = userSession.getLoggedInUser();
        if (user != null) {
            welcomeLabel.setText("Welcome, " + user.getUsername() + "!");
        }

        // --- ROW STYLING LOGIC ---
        transactionsTable.setRowFactory(tv -> new TableRow<Transaction>() {
            @Override
            protected void updateItem(Transaction item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    // 1. Check for "Recent" (Today's Date)
                    if (item.getDate().equals(LocalDate.now())) {
                        setStyle("-fx-background-color: #fff9c4;"); // Pale Yellow
                    }
                    // 2. Check for Income/Expense
                    else if (item.getType() == TransactionType.INCOME) {
                        setStyle("-fx-background-color: #c8e6c9;"); // Light Green
                    } else if (item.getType() == TransactionType.EXPENSE) {
                        setStyle("-fx-background-color: #ffcdd2;"); // Light Red
                    }
                    // 3. Default (for old dates that aren't income/expense?)
                    else {
                        setStyle("");
                    }
                }
            }
        });
        // --- END OF ROW STYLING ---

        // Setup table columns
        dateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDate()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getDescription() != null ? cellData.getValue().getDescription() : ""));
        amountColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAmount()));
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getCategory() != null ? cellData.getValue().getCategory().toString() : ""));

        // Store the transaction history view
        transactionHistoryView = mainBorderPane.getCenter();

        // Load the Calendar as the default home screen
        handleViewCalendar(null);

        // Load table data
        refreshTransactionTable();
    }

    public void refreshTransactionTable() {
        User user = userSession.getLoggedInUser();
        if (user != null) {
            ObservableList<Transaction> transactions = FXCollections.observableArrayList(
                    transactionRepository.findByUser(user));
            transactionsTable.setItems(transactions);
        }
    }

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent view = fxmlLoader.load();
            mainBorderPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        userSession.logout();
        try {
            viewSwitcher.switchTo("/fxml/LoginView.fxml", "Login");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleViewCalendar(ActionEvent event) {
        System.out.println("View Calendar menu clicked!");
        loadView("/fxml/CalendarView.fxml");
    }

    @FXML
    private void handleViewStatistics(ActionEvent event) {
        System.out.println("View Statistics menu clicked!");
        loadView("/fxml/StatisticsView.fxml");
    }

    @FXML
    private void handleShowAbout(ActionEvent event) {
        System.out.println("Show About menu clicked!");
        loadView("/fxml/AboutView.fxml");
    }

    // --- THIS METHOD IS NOW PUBLIC ---
    @FXML
    public void handleViewTransactionHistory(ActionEvent event) {
        System.out.println("View Transaction History clicked!");
        mainBorderPane.setCenter(transactionHistoryView);
        refreshTransactionTable();
    }
}