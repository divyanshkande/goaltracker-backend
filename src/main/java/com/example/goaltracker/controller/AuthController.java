package com.example.goaltracker.controller;

import com.example.goaltracker.UserDetailsServiceImpl;
import com.example.goaltracker.model.AuthRequest;
import com.example.goaltracker.model.User;
import com.example.goaltracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    // ✅ LOGIN endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            response.put("message", "Login successful");
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            response.put("error", "Login failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    // ✅ REGISTER endpoint
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            response.put("error", "Username already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        response.put("message", "User registered successfully");
        return ResponseEntity.ok(response);
    }
}
