package com.example.goaltracker.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.goaltracker.model.Goal;
import com.example.goaltracker.model.User;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUserId(Long userId);
    List<Goal> findByUser(User user);
}
