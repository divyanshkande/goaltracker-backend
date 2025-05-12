package com.example.goaltracker.controller;



import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.goaltracker.GoalService;
import com.example.goaltracker.model.Goal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;
    private static final Logger logger = LoggerFactory.getLogger(GoalController.class);

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @GetMapping
    public ResponseEntity<List<Goal>> getGoals() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null ? auth.getName() : null;
            logger.debug("Fetching goals for username: {}", username);

            if (username == null) {
                logger.error("No authenticated user found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(List.of());
            }

            List<Goal> goals = goalService.getAllGoalsByUsername(username);
            return ResponseEntity.ok(goals);
        } catch (Exception e) {
            logger.error("Error fetching goals: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }

    @PostMapping
    public ResponseEntity<Goal> addGoal(@RequestBody Goal goal) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null ? auth.getName() : null;
            logger.debug("Adding goal for username: {}", username);

            if (username == null) {
                logger.error("No authenticated user found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            Goal savedGoal = goalService.addGoal(goal, username);
            return ResponseEntity.ok(savedGoal);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            logger.error("Error adding goal: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null ? auth.getName() : null;
            logger.debug("Deleting goal with ID {} for username: {}", id, username);

            if (username == null) {
                logger.error("No authenticated user found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            goalService.deleteGoal(id, username);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            logger.error("Unauthorized: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            logger.error("Error deleting goal: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Goal> getGoalById(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null ? auth.getName() : null;
            logger.debug("Fetching goal with ID {} for username: {}", id, username);

            if (username == null) {
                logger.error("No authenticated user found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Optional<Goal> goal = goalService.getGoalById(id, username);
            return goal.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error fetching goal: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Goal> updateGoal(@PathVariable Long id, @RequestBody Goal updatedGoal) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null ? auth.getName() : null;
            logger.debug("Updating goal with ID {} for username: {}", id, username);

            if (username == null) {
                logger.error("No authenticated user found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Optional<Goal> goal = goalService.updateGoal(id, updatedGoal, username);
            return goal.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (SecurityException e) {
            logger.error("Unauthorized: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            logger.error("Error updating goal: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Goal> toggleGoalCompletion(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null ? auth.getName() : null;
            logger.debug("Toggling goal with ID {} for username: {}", id, username);

            if (username == null) {
                logger.error("No authenticated user found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Optional<Goal> goal = goalService.toggleGoalCompletion(id, username);
            return goal.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (SecurityException e) {
            logger.error("Unauthorized: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            logger.error("Error toggling goal: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
