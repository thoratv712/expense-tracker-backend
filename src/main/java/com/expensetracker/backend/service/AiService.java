package com.expensetracker.backend.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.expensetracker.backend.client.GeminiContent;
import com.expensetracker.backend.client.GeminiPart;
import com.expensetracker.backend.model.Expense;
import com.expensetracker.backend.model.User;
import com.expensetracker.backend.repository.ExpenseRepository;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class AiService {
	
	private final ExpenseRepository expenseRepository;
    private final RestClient restClient;
	private final ObjectMapper objectMapper;

	    @Value("${gemini.api.key}")
	    private String apiKey;

	    @Value("${gemini.api.url}")
	    private String apiUrl;


	    public AiService(ExpenseRepository expenseRepository, RestClient.Builder restClientBuilder) {
	        this.expenseRepository = expenseRepository;
	        this.restClient = restClientBuilder.build();
	        this.objectMapper = new ObjectMapper();
	    }


	    public String askQuestion(String question, LocalDate startDate, LocalDate endDate, User currentUser) {
	        LocalDate today = LocalDate.now();
	        LocalDate effectiveStart = (startDate != null) ? startDate : today.withDayOfMonth(1);
	        LocalDate effectiveEnd = (endDate != null) ? endDate : today;

	        List<Expense> expenses = expenseRepository.findByUserAndTransactionDateBetween(
	                currentUser, effectiveStart, effectiveEnd);

	        String summary = buildSpendingSummary(expenses);

	        String prompt = "Here is the user's spending summary from " + effectiveStart
	                + " to " + effectiveEnd + ":\n" + summary
	                + "\n\nNow answer this question based on that data: " + question;

	        Map<String, Object> requestBody = new HashMap<>();
	        requestBody.put("system_instruction", Map.of(
	                "parts", List.of(new GeminiPart(
	                        "You are a helpful personal finance assistant. All amounts are in Indian Rupees (INR), "
	                        + "and you must always use the ₹ symbol when stating amounts. "
	                        + "Answer clearly and concisely based only on the spending data provided."))
	        ));
	        requestBody.put("contents", List.of(
	                new GeminiContent("user", List.of(new GeminiPart(prompt)))
	        ));

	        int maxRetries = 3;
	        int waitMs = 2000;

	        for (int attempt = 1; attempt <= maxRetries; attempt++) {
	            try {
	                String rawResponse = restClient.post()
	                        .uri(apiUrl)
	                        .header("x-goog-api-key", apiKey)
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .body(requestBody)
	                        .retrieve()
	                        .body(String.class);

	                JsonNode root = objectMapper.readTree(rawResponse);
	                return root.path("candidates")
	                           .path(0)
	                           .path("content")
	                           .path("parts")
	                           .path(0)
	                           .path("text")
	                           .asText();

	            } catch (Exception e) {
	                System.out.println("Gemini attempt " + attempt + " failed: " + e.getMessage());

	                if (attempt == maxRetries) {
	                    return "The AI assistant is experiencing high demand right now. "
	                            + "Please wait a moment and try again.";
	                }

	                try {
	                    Thread.sleep(waitMs);
	                    waitMs *= 2;
	                } catch (InterruptedException ie) {
	                    Thread.currentThread().interrupt();
	                    return "Request interrupted. Please try again.";
	                }
	            }
	        }

	        return "The AI assistant is temporarily unavailable.";
	    }
    
    
    
    private String buildSpendingSummary(List<Expense> expenses) {
        Map<String, BigDecimal> totalsByCategory = new HashMap<>();

        // 1. Loop through `expenses`. For each one, get its category name
        //    (expense.getCategory().getName()) and its amount, and merge
        //    it into totalsByCategory using the pattern above.
        
        // Step 1: Calculate total spending per category
        for (Expense expense : expenses) {
            totalsByCategory.merge(
                expense.getCategory().getName(),
                expense.getAmount(),
                BigDecimal::add
            );
        }

        StringBuilder sb = new StringBuilder();
        BigDecimal grandTotal = BigDecimal.ZERO;

        // 2. Loop through totalsByCategory.entrySet(). For each entry, append
        //    a line like "CategoryName: Amount\n" to sb, and add that amount
        //    onto grandTotal (BigDecimal also has .add(...) for this).

        // Step 2: Build summary and calculate grand total
        for (Map.Entry<String, BigDecimal> entry : totalsByCategory.entrySet()) {
        sb.append(entry.getKey())
            .append(": ₹")
            .append(entry.getValue())
            .append("\n");
        
            grandTotal = grandTotal.add(entry.getValue());
            sb.append("Total: ₹").append(grandTotal);
        }
        
        // 3. Append a final line: "Total: " + grandTotal
        sb.append("Total: ").append(grandTotal);
        
        
        return sb.toString();
    }

}
