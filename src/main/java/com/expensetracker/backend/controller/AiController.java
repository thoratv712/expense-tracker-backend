package com.expensetracker.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expensetracker.backend.dto.AiRequest;
import com.expensetracker.backend.dto.AiResponse;
import com.expensetracker.backend.model.User;
import com.expensetracker.backend.service.AiService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/ask")
    public ResponseEntity<AiResponse> ask(@Valid @RequestBody AiRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String answer = aiService.askQuestion(request.getQuestion(), request.getStartDate(), request.getEndDate(), currentUser);
        return ResponseEntity.ok(new AiResponse(answer));
    }
}