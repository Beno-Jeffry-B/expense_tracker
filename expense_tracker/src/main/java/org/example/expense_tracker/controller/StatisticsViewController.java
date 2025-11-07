package org.example.expense_tracker.controller;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import org.example.expense_tracker.model.Category;
import org.example.expense_tracker.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class StatisticsViewController {

    @FXML
    private PieChart categoryPieChart;

    @FXML
    private BarChart<String, Number> dailySpendingBarChart;

    // --- NEW FIELDS ---
    @FXML
    private LineChart<String, Number> expenseTrendChart;

    @FXML
    private LineChart<String, Number> incomeTrendChart;
    // --- END OF NEW FIELDS ---

    private final TransactionService transactionService;

    @Autowired
    public StatisticsViewController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @FXML
    public void initialize() {
        loadPieChartData();
        loadBarChartData();
        loadExpenseTrendChartData(); // <-- NEW
        loadIncomeTrendChartData(); // <-- NEW
    }

    private void loadPieChartData() {
        Map<Category, Double> expenseData = transactionService.getExpenseBreakdown();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<Category, Double> entry : expenseData.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey().toString(), entry.getValue()));
        }
        categoryPieChart.setData(pieChartData);
    }

    private void loadBarChartData() {
        Map<LocalDate, Double> dailySpending = transactionService.getDailySpending();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Daily Expenses");

        // Sort by date to make the chart logical
        List<Map.Entry<LocalDate, Double>> sortedEntries = dailySpending.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());

        for (Map.Entry<LocalDate, Double> entry : sortedEntries) {
            series.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
        }
        dailySpendingBarChart.getData().clear();
        dailySpendingBarChart.getData().add(series);
    }

    // --- NEW METHOD ---
    private void loadExpenseTrendChartData() {
        Map<LocalDate, Double> trendData = transactionService.getExpenseTrend();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Expenses");

        List<Map.Entry<LocalDate, Double>> sortedEntries = trendData.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());

        for (Map.Entry<LocalDate, Double> entry : sortedEntries) {
            series.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
        }
        expenseTrendChart.getData().clear();
        expenseTrendChart.getData().add(series);
    }

    // --- NEW METHOD ---
    private void loadIncomeTrendChartData() {
        Map<LocalDate, Double> trendData = transactionService.getIncomeTrend();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Income");

        List<Map.Entry<LocalDate, Double>> sortedEntries = trendData.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());

        for (Map.Entry<LocalDate, Double> entry : sortedEntries) {
            series.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
        }
        incomeTrendChart.getData().clear();
        incomeTrendChart.getData().add(series);
    }
}
