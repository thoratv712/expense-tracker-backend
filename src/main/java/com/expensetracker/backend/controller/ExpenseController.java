package com.expensetracker.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expensetracker.backend.dto.ExpenseRequest;
import com.expensetracker.backend.dto.ExpenseResponse;
import com.expensetracker.backend.model.User;
import com.expensetracker.backend.service.ExpenseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(@Valid @RequestBody ExpenseRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ExpenseResponse response = expenseService.createExpense(request, currentUser);

        return ResponseEntity.status(201).body(response);
    }
    
    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getAllExpenses() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(expenseService.getAllExpenses(currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> getExpenseById(@PathVariable Long id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(expenseService.getExpenseById(id, currentUser));
    }
    
    @PutMapping("/{id}")
     public ResponseEntity<ExpenseResponse> updateExpense(@PathVariable Long id,@Valid @RequestBody ExpenseRequest request){
    	 User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	 return ResponseEntity.ok(expenseService.updateExpense(id, request, currentUser));
     }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        expenseService.deleteExpense(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}