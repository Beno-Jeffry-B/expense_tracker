package org.example.expense_tracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.example.expense_tracker.service.ViewSwitcher;

import java.io.IOException;

public class JavaFxApplication extends Application {
    private ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        this.springContext = new SpringApplicationBuilder(ExpenseTrackerApplication.class).run();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);

        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        primaryStage.setTitle("Expense Tracker - Login");
        primaryStage.setScene(scene);
        primaryStage.show();

        ViewSwitcher viewSwitcher = springContext.getBean(ViewSwitcher.class);
        viewSwitcher.setStage(primaryStage);
    }

    @Override
    public void stop() {
        if (springContext != null) {
            springContext.close();
        }
    }
}
