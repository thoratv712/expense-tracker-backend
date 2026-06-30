package com.expensetracker.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ExpenseResponse {
	
	private Long id;
	private BigDecimal amount;
	private String description;
	private LocalDate transactionDate;
	private String categoryName;
	private LocalDateTime createdAt;
	
	
	
	public ExpenseResponse(Long id, BigDecimal amount, String description, LocalDate transactionDate,
			String categoryName, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.amount = amount;
		this.description = description;
		this.transactionDate = transactionDate;
		this.categoryName = categoryName;
		this.createdAt = createdAt;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public LocalDate getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(LocalDate transactionDate) {
		this.transactionDate = transactionDate;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	

}
