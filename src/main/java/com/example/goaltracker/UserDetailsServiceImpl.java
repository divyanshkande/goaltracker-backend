package com.example.goaltracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.goaltracker.repository.UserRepository;

import java.util.ArrayList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository; // Your JPA repository

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.example.goaltracker.model.User user = userRepository.findByUsername(username)
                   .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(), // ✅ should be encoded in DB
            new ArrayList<>()   // authorities/roles if needed
        );
    }
}
