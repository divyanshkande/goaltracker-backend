package com.example.goaltracker;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.goaltracker.model.Goal;
@Service
public class GoalService {
	private final GoalRepository goalRepo;
	  public GoalService(GoalRepository goalRepo) {
	        this.goalRepo = goalRepo;
	    }



	public List<Goal> getAllGoals() {


		return goalRepo.findAll();
	}

	public Goal addGoal(Goal goal) {


		return goalRepo.save(goal);
	}

	public void deleteGoal(Long id) {
		goalRepo.deleteById(id);


		
	}

	public Optional<Goal> updateGoal(Long id, Goal updatedGoal) {
		 return goalRepo.findById(id).map(goal -> {
	            goal.setTitle(updatedGoal.getTitle());
	            goal.setDescription(updatedGoal.getDescription());
	            goal.setCompleted(updatedGoal.isCompleted());
	            return goalRepo.save(goal);
	        });




	}
	

}
