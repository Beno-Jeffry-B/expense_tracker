package org.example.expense_tracker.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker; // <-- Uses standard JavaFX
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.time.LocalDate;

@Controller
public class CalendarViewController {

    @FXML
    private DatePicker datePicker; // <-- Now a standard DatePicker

    private final ApplicationContext applicationContext;
    private final MainViewController mainViewController;

    @Autowired
    public CalendarViewController(ApplicationContext applicationContext, MainViewController mainViewController) {
        this.applicationContext = applicationContext;
        this.mainViewController = mainViewController;
    }

    @FXML
    public void initialize() {
        // Set the default date to today
        datePicker.setValue(LocalDate.now());

        // Add a listener that fires when the user selects a date
        datePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                // Open the transaction form for the new date
                openTransactionForm(newDate);
            }
        });
    }

    private void openTransactionForm(LocalDate date) {
        try {
            // 1. Load the FXML using Spring's context
            FXMLLoader fXMLloader = new FXMLLoader(getClass().getResource("/fxml/TransactionForm.fxml"));
            fXMLloader.setControllerFactory(applicationContext::getBean);
            Parent root = fXMLloader.load();

            // 2. Get the controller of the popup form
            TransactionFormController controller = fXMLloader.getController();

            // 3. Pass the clicked date to the form
            controller.setDate(date);

            // 4. Create and show the popup window
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Transaction");
            stage.setScene(new Scene(root));

            // 5. IMPORTANT: Wait for the popup to close...
            stage.showAndWait();

            // 6. ...and THEN refresh the main transaction table
            mainViewController.refreshTransactionTable();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}