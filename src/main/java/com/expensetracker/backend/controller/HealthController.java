/**
 * 
 */
package com.expensetracker.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 
 */
@RestController
public class HealthController {

	@GetMapping("/api/health")
	public String getMethodName() {
		return new String("Confirming the backend is alive.");
	}
	
	@GetMapping("/api/method")
	public String anotherMethod() {
		return new String("Another method");
	}

}
