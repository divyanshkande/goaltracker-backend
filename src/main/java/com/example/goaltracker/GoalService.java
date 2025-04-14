package com.example.goaltracker;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.goaltracker.model.Goal;
import com.example.goaltracker.repository.GoalRepository;

import java.util.List;
import java.util.Optional;

@Service
public class GoalService {

    private final GoalRepository goalRepository;

    @Autowired
    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    // Fetch all goals
    public List<Goal> getAllGoals() {
        return goalRepository.findAll();
    }

    // Add a new goal with validation
    public Goal addGoal(Goal goal) {
        try {
            // Validate goal fields before saving
            if (goal.getTitle() == null || goal.getTitle().isEmpty()) {
                throw new IllegalArgumentException("Goal title cannot be empty");
            }
            if (goal.getDescription() == null || goal.getDescription().isEmpty()) {
                throw new IllegalArgumentException("Goal description cannot be empty");
            }

            // Save the goal
            return goalRepository.save(goal);
        } catch (IllegalArgumentException e) {
            // Handle validation exceptions
            throw new RuntimeException("Validation error: " + e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions
            throw new RuntimeException("Error saving goal: " + e.getMessage());
        }
    }

    // Delete a goal by ID
    public void deleteGoal(Long id) {
        try {
            goalRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting goal with id " + id + ": " + e.getMessage());
        }
    }

    // Update an existing goal
    public Optional<Goal> updateGoal(Long id, Goal goal) {
        try {
            Optional<Goal> existingGoal = goalRepository.findById(id);
            if (existingGoal.isPresent()) {
                Goal updatedGoal = existingGoal.get();
                updatedGoal.setTitle(goal.getTitle());
                updatedGoal.setDescription(goal.getDescription());
                updatedGoal.setCompleted(goal.isCompleted());
                return Optional.of(goalRepository.save(updatedGoal));
            } else {
                throw new RuntimeException("Goal with ID " + id + " not found.");
            }
        } catch (Exception e) {
            // Handle unexpected errors
            throw new RuntimeException("Error updating goal: " + e.getMessage());
        }
    }
}
