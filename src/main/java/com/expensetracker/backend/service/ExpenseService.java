package com.expensetracker.backend.service;

import com.expensetracker.backend.exception.ResourceNotFoundException;

import java.util.List;

import org.springframework.stereotype.Service;

import com.expensetracker.backend.dto.ExpenseRequest;
import com.expensetracker.backend.dto.ExpenseResponse;
import com.expensetracker.backend.model.Category;
import com.expensetracker.backend.model.Expense;
import com.expensetracker.backend.model.User;
import com.expensetracker.backend.repository.CategoryRepository;
import com.expensetracker.backend.repository.ExpenseRepository;

@Service
public class ExpenseService {
	
	private ExpenseRepository expenseRepository;
	private CategoryRepository categoryRepository;
	
	public ExpenseService(ExpenseRepository expenseRepository, CategoryRepository categoryRepository) {
		super();
		this.expenseRepository = expenseRepository;
		this.categoryRepository = categoryRepository;
	}
	
	private ExpenseResponse mapToResponse(Expense expense) {
		return new ExpenseResponse(expense.getId(),expense.getAmount(),expense.getDescription(),
				expense.getTransactionDate(),expense.getCategory().getName(),expense.getCreatedAt());
	}
	
	public ExpenseResponse createExpense(ExpenseRequest request, User currentUser) {
		Category category = categoryRepository.findById(request.getCategoryId())
		        .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
		
		Expense expenseToSave = new Expense(null, currentUser, category, request.getAmount(),
		        request.getDescription(), request.getTransactionDate(), null);

		Expense savedExpense = expenseRepository.save(expenseToSave);

		return mapToResponse(savedExpense);
	}

	
	public List<ExpenseResponse> getAllExpenses(User currentUser){
		return expenseRepository.findByUser(currentUser)
				.stream()
				.map(this::mapToResponse)
				.toList();
	}
	
	public ExpenseResponse getExpenseById(Long id, User currentUser) {
	
		return mapToResponse(getOwnedExpense(id, currentUser));
	
	}
	
	private Expense getOwnedExpense(Long id, User currentUser) {
	    // move the find + ownership-check logic from getExpenseById here
		Expense expense = expenseRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
	
		if(!expense.getUser().getId().equals(currentUser.getId())) {
			 throw new ResourceNotFoundException("Expense not found");
		}
		
		return expense;
	}
	
	public ExpenseResponse updateExpense(Long id, ExpenseRequest request, User currentUser) {
	    // 1. Get the existing expense using your new getOwnedExpense helper
		Expense existingExpense = getOwnedExpense(id, currentUser);
		
	    // 2. Look up the (possibly new) category by request.getCategoryId(), same orElseThrow pattern as createExpense
		Category category = categoryRepository.findById(request.getCategoryId())
		        .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
		
		// 3. Use the setters you generated earlier to update: amount, description, transactionDate, category
	    existingExpense.setAmount(request.getAmount());
	    existingExpense.setCategory(category);
	    existingExpense.setDescription(request.getDescription());
		existingExpense.setTransactionDate(request.getTransactionDate());

		
		// 4. Save it again with expenseRepository.save(...) — this time it'll UPDATE, not INSERT
		Expense savedExpense =  expenseRepository.save(existingExpense);
		
		// 5. Return mapToResponse(...) of whatever .save() returned
	    return mapToResponse(savedExpense);
	    
	}
	
	
	public void deleteExpense(Long id, User currentUser) {
	    Expense expense = getOwnedExpense(id, currentUser);
	    expenseRepository.delete(expense);
	}
}
