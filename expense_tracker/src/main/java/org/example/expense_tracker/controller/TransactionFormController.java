package org.example.expense_tracker.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.expense_tracker.model.Category;
import org.example.expense_tracker.model.TransactionType;
import org.example.expense_tracker.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
public class TransactionFormController {

    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField amountField;
    @FXML
    private ComboBox<TransactionType> typeComboBox;
    @FXML
    private ComboBox<Category> categoryComboBox;
    @FXML
    private Label errorLabel;

    private final TransactionService transactionService;

    // --- NEW FIELD ---
    private MainViewController mainViewController;

    @Autowired
    public TransactionFormController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @FXML
    public void initialize() {
        datePicker.setValue(LocalDate.now());
        typeComboBox.getItems().setAll(TransactionType.values());
        categoryComboBox.getItems().setAll(Category.values());
    }

    // --- NEW METHOD ---
    // This allows the CalendarController to pass us the MainViewController
    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public void setDate(LocalDate date) {
        if (date != null) {
            datePicker.setValue(date);
        }
    }

    @FXML
    private void handleSave() {
        errorLabel.setText("");
        LocalDate date = datePicker.getValue();
        String description = descriptionField.getText();
        String amountStr = amountField.getText();
        TransactionType type = typeComboBox.getValue();
        Category category = categoryComboBox.getValue();

        if (date == null || description == null || description.trim().isEmpty() ||
                amountStr == null || amountStr.trim().isEmpty() ||
                type == null || category == null) {
            errorLabel.setText("All fields are required.");
            return;
        }

        if (date.isAfter(LocalDate.now())) {
            errorLabel.setText("Cannot add transactions for a future date.");
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                errorLabel.setText("Amount must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Invalid amount. Please enter a number.");
            return;
        }

        try {
            transactionService.saveTransaction(amount, type, category, date, description);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Transaction saved successfully!");
            alert.showAndWait();

            // --- NEW REDIRECT LOGIC ---
            if (mainViewController != null) {
                // This calls the public method to switch the view
                mainViewController.handleViewTransactionHistory(null);
            }
            // --- END OF NEW LOGIC ---

            closeWindow();

        } catch (Exception e) {
            errorLabel.setText("Failed to save transaction: " + e.getMessage());
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) errorLabel.getScene().getWindow();
        stage.close();
    }
}