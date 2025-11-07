package org.example.expense_tracker.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label; // <-- IMPORTANT
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.expense_tracker.model.User;
import org.example.expense_tracker.service.UserService;
import org.example.expense_tracker.service.UserSession;
import org.example.expense_tracker.service.ViewSwitcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException; // <-- IMPORTANT
import java.util.Optional;

@Controller
public class LoginViewController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel; // <-- Field for the error label

    private final UserService userService;
    private final ViewSwitcher viewSwitcher;
    private final UserSession userSession;

    @Autowired
    public LoginViewController(UserService userService, ViewSwitcher viewSwitcher, UserSession userSession) {
        this.userService = userService;
        this.viewSwitcher = viewSwitcher;
        this.userSession = userSession;
    }

    @FXML
    public void onLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            if (errorLabel != null)
                errorLabel.setText("Username and password are required.");
            return;
        }

        Optional<User> user = userService.loginUser(username, password);

        // --- THIS IS THE FIX ---
        if (user.isPresent()) {
            // Login Successful!
            if (errorLabel != null)
                errorLabel.setText(""); // Clear error

            // Set the user in the session
            userSession.login(user.get());

            // Switch to the REAL main view
            viewSwitcher.switchTo("/fxml/MainView.fxml", "Expense Tracker");
        } else {
            // Login Failed
            if (errorLabel != null)
                errorLabel.setText("Invalid username or password.");
            passwordField.clear();
        }
    }

    @FXML
    public void onGoToSignup(ActionEvent event) {
        // This part navigates to the sign-up page
        viewSwitcher.switchTo("/fxml/SignupView.fxml", "Sign Up");
    }
}
