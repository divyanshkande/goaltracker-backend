package com.example.goaltracker.controller;

import com.example.goaltracker.JWTService;
import com.example.goaltracker.model.AuthResponse;
import com.example.goaltracker.model.User;
import com.example.goaltracker.repository.UserRepository;
import com.example.goaltracker.service.UserService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService service;

    @Autowired
    private JWTService jwtService;
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody User user, HttpServletResponse response) {
        try {
            // Authenticate with Spring Security
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            // Fetch user from DB using Optional
            Optional<User> authenticatedUserOptional = userRepository.findByUsername(user.getUsername());
            
            // If the user is not found, return a 401
            if (authenticatedUserOptional.isEmpty()) {
                return ResponseEntity.status(401).body(new AuthResponse(null, null));
            }

            // Get the authenticated user from Optional
            User authenticatedUser = authenticatedUserOptional.get();

            // Generate JWT
            String token = jwtService.generateToken(authenticatedUser.getUsername());

            // Set token in cookie
            ResponseCookie cookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(3600)
                    .sameSite("Lax")
                    .build();

            response.setHeader("Set-Cookie", cookie.toString());

            return ResponseEntity.ok(new AuthResponse(token, authenticatedUser.getUsername()));

        } catch (Exception e) {
            return ResponseEntity.status(401).body(new AuthResponse(null, null));
        }
    }



    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletResponse response) {
        // Invalidate the cookie
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false) // Set to true in production
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.setHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(new AuthResponse(null, null));
    }
}