package com.example.goaltracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.goaltracker.JWTService;
import com.example.goaltracker.model.User;
import com.example.goaltracker.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JWTService jwtservice;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public User register(User user) {
        // Hash the password before saving the user
        user.setPassword(encoder.encode(user.getPassword()));
        return repo.save(user);
    }

    public User verify(User user) {
        try {
            Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            if (authentication.isAuthenticated()) {
                return repo.findByUsername(user.getUsername()).orElse(null); // ✅ return the full User object
            }

            return null;
        } catch (AuthenticationException e) {
            return null; // Gracefully return null on failure
        }
    }

}
