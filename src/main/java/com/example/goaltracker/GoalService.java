package com.example.goaltracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.goaltracker.model.Goal;
import com.example.goaltracker.model.User;
import com.example.goaltracker.repository.GoalRepository;
import com.example.goaltracker.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(GoalService.class);

    @Autowired
    public GoalService(GoalRepository goalRepository, UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
        logger.debug("GoalService initialized with repositories: {}, {}", goalRepository, userRepository);
    }

    // Fetch all goals based on logged-in user's username (from token)
    @Transactional(readOnly = true)
    public List<Goal> getAllGoalsByUsername(String username) {
        // Validate username
        if (username == null || username.trim().isEmpty()) {
            logger.warn("Username is null or empty, returning empty goal list");
            return Collections.emptyList();
        }

        // Fetch"user
        logger.debug("Fetching user with username: {}", username);
        Optional<User> userOptional;
        try {
            userOptional = userRepository.findByUsername(username);
        } catch (Exception e) {
            logger.error("Error fetching user {}: {}", username, e.getMessage(), e);
            return Collections.emptyList();
        }

        if (userOptional.isEmpty()) {
            logger.warn("User not found with username: {}, returning empty goal list", username);
            return Collections.emptyList();
        }

        // Fetch goals
        User user = userOptional.get();
        logger.debug("Fetching goals for user: {}", username);
        try {
            List<Goal> goals = goalRepository.findByUser(user); // Line 43 (adjusted)
            if (goals == null) {
                logger.warn("GoalRepository.findByUser returned null for user: {}", username);
                return Collections.emptyList();
            }
            logger.debug("Found {} goals for user: {}", goals.size(), username);
            return goals;
        } catch (Exception e) {
            logger.error("Error fetching goals for user {}: {}", username, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    // Add a new goal
    @Transactional
    public Goal addGoal(Goal goal, String username) {
        // Validate goal
        if (goal == null) {
            logger.error("Goal object is null");
            throw new IllegalArgumentException("Goal cannot be null");
        }
        if (goal.getTitle() == null || goal.getTitle().isEmpty()) {
            logger.error("Goal title is null or empty");
            throw new IllegalArgumentException("Goal title cannot be empty");
        }
        if (goal.getDescription() == null || goal.getDescription().isEmpty()) {
            logger.error("Goal description is null or empty");
            throw new IllegalArgumentException("Goal description cannot be empty");
        }

        // Validate username
        if (username == null || username.trim().isEmpty()) {
            logger.error("Username is null or empty");
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        // Associate goal with user
        logger.debug("Fetching user with username: {}", username);
        Optional<User> userOptional;
        try {
            userOptional = userRepository.findByUsername(username); // Line 71
        } catch (Exception e) {
            logger.error("Error fetching user {}: {}", username, e.getMessage(), e);
            throw new IllegalArgumentException("Failed to fetch user: " + username, e);
        }

        if (userOptional.isEmpty()) {
            logger.warn("User not found with username: {}", username);
            throw new IllegalArgumentException("User not found with username: " + username);
        }

        User user = userOptional.get();
        goal.setUser(user);

        logger.debug("Saving goal for user: {}", username);
        try {
            return goalRepository.save(goal);
        } catch (Exception e) {
            logger.error("Error saving goal for user {}: {}", username, e.getMessage(), e);
            throw new IllegalArgumentException("Failed to save goal for user: " + username, e);
        }
    }

    // Delete a goal by ID
    @Transactional
    public void deleteGoal(Long id, String username) {
        if (id == null) {
            logger.error("Goal ID is null");
            throw new IllegalArgumentException("Goal ID cannot be null");
        }

        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Goal not found with ID: {}", id);
                    return new IllegalArgumentException("Goal not found with ID: " + id);
                });

        if (!goal.getUser().getUsername().equals(username)) {
            logger.error("Unauthorized attempt to delete goal ID {} by user {}", id, username);
            throw new SecurityException("Unauthorized to delete this goal");
        }

        logger.debug("Deleting goal with ID: {}", id);
        goalRepository.deleteById(id);
    }

    // Get a goal by ID
    @Transactional(readOnly = true)
    public Optional<Goal> getGoalById(Long id, String username) {
        if (id == null) {
            logger.error("Goal ID is null");
            return Optional.empty();
        }

        return goalRepository.findById(id)
                .filter(goal -> goal.getUser().getUsername().equals(username));
    }

    // Update a goal
    @Transactional
    public Optional<Goal> updateGoal(Long id, Goal updatedGoal, String username) {
        if (id == null) {
            logger.error("Goal ID is null");
            return Optional.empty();
        }
        if (updatedGoal == null) {
            logger.error("Updated goal object is null");
            return Optional.empty();
        }

        return goalRepository.findById(id).map(existing -> {
            if (!existing.getUser().getUsername().equals(username)) {
                logger.error("Unauthorized attempt to update goal ID {} by user {}", id, username);
                throw new SecurityException("Unauthorized to update this goal");
            }

            if (updatedGoal.getTitle() == null || updatedGoal.getTitle().isEmpty()) {
                logger.error("Updated goal title is null or empty");
                throw new IllegalArgumentException("Goal title cannot be empty");
            }
            if (updatedGoal.getDescription() == null || updatedGoal.getDescription().isEmpty()) {
                logger.error("Updated goal description is null or empty");
                throw new IllegalArgumentException("Goal description cannot be empty");
            }

            existing.setTitle(updatedGoal.getTitle());
            existing.setDescription(updatedGoal.getDescription());
            existing.setCompleted(updatedGoal.isCompleted());
            logger.debug("Updating goal with ID: {}", id);
            return goalRepository.save(existing);
        });
    }

    // Toggle goal completion status
    @Transactional
    public Optional<Goal> toggleGoalCompletion(Long id, String username) {
        if (id == null) {
            logger.error("Goal ID is null");
            return Optional.empty();
        }

        return goalRepository.findById(id).map(goal -> {
            if (!goal.getUser().getUsername().equals(username)) {
                logger.error("Unauthorized attempt to toggle goal ID {} by user {}", id, username);
                throw new SecurityException("Unauthorized to toggle this goal");
            }

            goal.setCompleted(!goal.isCompleted());
            logger.debug("Toggling completion status for goal ID: {}", id);
            return goalRepository.save(goal);
        });
    }
}