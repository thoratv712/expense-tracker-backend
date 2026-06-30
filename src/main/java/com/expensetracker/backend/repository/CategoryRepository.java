package com.expensetracker.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.expensetracker.backend.model.Category;
import com.expensetracker.backend.model.User;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	
	List<Category> findByUser(User user);
}
