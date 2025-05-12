package com.example.goaltracker;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;

@Service
public class JWTService {

    private static final Logger logger = LoggerFactory.getLogger(JWTService.class);

    private final String secretKey;
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 30; // 30 hours

    public JWTService(@Value("${jwt.secret}") String secretKey) {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            logger.error("JWT secret key is null or empty. Please define 'jwt.secret' in application.properties or environment variables.");
            throw new IllegalArgumentException("JWT secret key is required.");
        }
        this.secretKey = secretKey;
        logger.debug("JWTService initialized with secret key (first 10 chars): {}", secretKey.substring(0, Math.min(10, secretKey.length())));
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        logger.debug("Generating token for username: {}", username);
        try {
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(username)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .signWith(getKey(), SignatureAlgorithm.HS256)
                    .compact();
            logger.debug("Token generated successfully for username: {}", username);
            return token;
        } catch (Exception e) {
            logger.error("Error generating token for username {}: {}", username, e.getMessage(), e);
            throw new RuntimeException("Failed to generate token", e);
        }
    }

    public String extractUserName(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            logger.error("Error extracting username from token: {}", e.getMessage(), e);
            return null;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.error("Error parsing token claims: {}", e.getMessage(), e);
            throw new RuntimeException("Invalid token", e);
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUserName(token);
            boolean isValid = username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token);
            logger.debug("Token validation for username {}: {}", username, isValid ? "valid" : "invalid");
            return isValid;
        } catch (Exception e) {
            logger.error("Error validating token for user {}: {}", userDetails.getUsername(), e.getMessage(), e);
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            final Date expiration = extractClaim(token, Claims::getExpiration);
            boolean expired = expiration.before(new Date());
            logger.debug("Token expiration check: expired={}", expired);
            return expired;
        } catch (Exception e) {
            logger.error("Error checking token expiration: {}", e.getMessage(), e);
            return true; // Treat as expired on error
        }
    }

    private SecretKey getKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            if (keyBytes.length < 32) {
                logger.error("JWT secret key is too short. Minimum length is 32 bytes for HS256.");
                throw new IllegalArgumentException("JWT secret key is too short.");
            }
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid JWT secret key format: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid JWT secret key. Ensure it is a valid Base64-encoded string.", e);
        }
    }
}