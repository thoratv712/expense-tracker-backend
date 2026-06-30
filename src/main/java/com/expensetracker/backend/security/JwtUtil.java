package com.expensetracker.backend.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secreteKeyString;
	
	@Value("${jwt.expiration-ms}")
	private long expirationMs;
	
	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(secreteKeyString.getBytes());
	}
	
	public String generateToken(String email) {
		return Jwts.builder()
				.subject(email)
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis()+expirationMs))
				.signWith(getSigningKey())
				.compact();
	}
	
	public String extractEmail(String token) {
		Claims claims = Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
		return claims.getSubject();
		
	}
	
	public boolean isTokenValid(String token) {
		try {
			extractEmail(token);
			return true;
		}catch(JwtException e) {
			return false;
		}
	}
}
