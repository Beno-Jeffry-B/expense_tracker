package org.example.expense_tracker.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ViewSwitcher {
    private final ApplicationContext applicationContext;
    private Stage primaryStage;

    @Autowired
    public ViewSwitcher(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void switchTo(String fxmlPath, String title) {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage not set on ViewSwitcher");
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(applicationContext::getBean);
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load view: " + fxmlPath, e);
        }
    }
}

