package com.expensetracker.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.expensetracker.backend.model.Expense;
import com.expensetracker.backend.model.User;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

	List<Expense> findByUser(User user);
	
	List<Expense> findByUserAndTransactionDateBetween(User user, LocalDate start, LocalDate end);
	
}
