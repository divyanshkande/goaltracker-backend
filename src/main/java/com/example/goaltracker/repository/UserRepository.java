package com.example.goaltracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.goaltracker.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
