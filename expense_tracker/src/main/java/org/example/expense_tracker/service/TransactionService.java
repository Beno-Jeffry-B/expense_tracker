package org.example.expense_tracker.service;

import org.example.expense_tracker.model.Category;
import org.example.expense_tracker.model.Transaction;
import org.example.expense_tracker.model.TransactionType;
import org.example.expense_tracker.model.User;
import org.example.expense_tracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserSession userSession;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, UserSession userSession) {
        this.transactionRepository = transactionRepository;
        this.userSession = userSession;
    }

    public Transaction saveTransaction(BigDecimal amount, TransactionType type, Category category, LocalDate date, String description) {
        User user = userSession.getLoggedInUser();
        if (user == null) {
            throw new IllegalStateException("No user logged in");
        }
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setCategory(category);
        transaction.setDate(date);
        transaction.setDescription(description);
        transaction.setUser(user);

        return transactionRepository.save(transaction);
    }

    private List<Transaction> getTransactionsForCurrentUser() {
        User user = userSession.getLoggedInUser();
        if (user == null) {
            return List.of();
        }
        return transactionRepository.findByUser(user);
    }

    public Map<Category, Double> getExpenseBreakdown() {
        return getTransactionsForCurrentUser().stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(t -> t.getAmount().doubleValue())
                ));
    }

    public Map<LocalDate, Double> getDailySpending() {
        return getTransactionsForCurrentUser().stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        Transaction::getDate,
                        Collectors.summingDouble(t -> t.getAmount().doubleValue())
                ));
    }

    public Map<LocalDate, Double> getExpenseTrend() {
        return getTransactionsForCurrentUser().stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        Transaction::getDate,
                        Collectors.summingDouble(t -> t.getAmount().doubleValue())
                ));
    }

    public Map<LocalDate, Double> getIncomeTrend() {
        return getTransactionsForCurrentUser().stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .collect(Collectors.groupingBy(
                        Transaction::getDate,
                        Collectors.summingDouble(t -> t.getAmount().doubleValue())
                ));
    }

    // NOTE: The getTotalIncomeForCurrentMonth() and getTotalExpenseForCurrentMonth() methods were removed.
}