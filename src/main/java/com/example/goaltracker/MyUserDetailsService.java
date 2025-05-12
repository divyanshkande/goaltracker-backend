package com.example.goaltracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.example.goaltracker.model.User;
import com.example.goaltracker.repository.UserRepository;

import java.util.Collections;
import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = repo.findByUsername(username); // Line 22 (corrected)

        User user = userOptional.orElseThrow(() -> 
            new UsernameNotFoundException("User not found with username: " + username)
        );

        // Convert your User entity into Spring Security's UserDetails
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList() // Or assign roles/authorities here
        );
    }
}