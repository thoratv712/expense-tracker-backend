package com.expensetracker.backend.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;

public class AiRequest {
	
	@NotBlank
	private String question;
	
	private LocalDate startDate ;
	
	private LocalDate endDate;

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	
	
	

}
