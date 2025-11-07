package org.example.expense_tracker.repository;

import org.example.expense_tracker.model.Transaction;
import org.example.expense_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(User user);
}

