package com.expensetracker.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expensetracker.backend.dto.CategoryRequest;
import com.expensetracker.backend.dto.CategoryResponse;
import com.expensetracker.backend.model.Category;
import com.expensetracker.backend.model.User;
import com.expensetracker.backend.repository.CategoryRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getListOfCategories() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<CategoryResponse> response = categoryRepository.findByUser(currentUser)
                .stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName()))
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> saveCategory(@Valid @RequestBody CategoryRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Category category = new Category(null, request.getName(), currentUser);
        Category savedCategory = categoryRepository.save(category);

        return ResponseEntity.status(201).body(new CategoryResponse(savedCategory.getId(), savedCategory.getName()));
    }
}