package com.example.goaltracker;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.goaltracker.model.Goal;

public interface GoalRepository extends JpaRepository<Goal,Long> {

}
