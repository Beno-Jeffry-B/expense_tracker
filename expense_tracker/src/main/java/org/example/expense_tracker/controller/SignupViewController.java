package org.example.expense_tracker.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.expense_tracker.service.UserService;
import org.example.expense_tracker.service.ViewSwitcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class SignupViewController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button signupButton;

    private final UserService userService;
    private final ViewSwitcher viewSwitcher;

    @Autowired
    public SignupViewController(UserService userService, ViewSwitcher viewSwitcher) {
        this.userService = userService;
        this.viewSwitcher = viewSwitcher;
    }

    @FXML
    public void onSignup(ActionEvent event) {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        try {
            userService.registerUser(username, email, password);
            viewSwitcher.switchTo("/fxml/LoginView.fxml", "Login");
        } catch (IllegalArgumentException ex) {
            usernameField.clear();
            usernameField.setPromptText(ex.getMessage());
        }
    }

    @FXML
    public void onGoToLogin(ActionEvent event) {
        viewSwitcher.switchTo("/fxml/LoginView.fxml", "Login");
    }
}

