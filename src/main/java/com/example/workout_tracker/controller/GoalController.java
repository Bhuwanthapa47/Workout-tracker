package com.example.workout_tracker.controller;

import com.example.workout_tracker.model.Exercise;
import com.example.workout_tracker.model.Goal;
import com.example.workout_tracker.model.User;
import com.example.workout_tracker.repository.ExerciseRepository;
import com.example.workout_tracker.repository.GoalRepository;
import com.example.workout_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @PostMapping
    public String createGoal(
            @RequestParam Long userId,
            @RequestParam Long exerciseId,
            @RequestParam Double targetWeight,
            @RequestParam String targetDate) {

        User user = userRepository.findById(userId).orElseThrow();
        Exercise exercise = exerciseRepository.findById(exerciseId).orElseThrow();

        Goal goal = new Goal();
        goal.setUser(user);
        goal.setExercise(exercise);
        goal.setTargetWeight(targetWeight);
        goal.setTargetDate(LocalDate.parse(targetDate));

        goalRepository.save(goal);

        return "Goal created!";
    }

    @GetMapping("/{userId}")
    public List<Goal> getGoals(@PathVariable Long userId) {
        return goalRepository.findByUserId(userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGoal(@PathVariable Long id) {
        goalRepository.deleteById(id);
        return ResponseEntity.ok("Goal deleted successfully");
    }

    @PostMapping("/complete/{goalId}")
    public ResponseEntity<String> completeGoal(@PathVariable Long goalId) {
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new RuntimeException("Goal not found"));
        goal.setCompleted(true);
        goalRepository.save(goal);
        return ResponseEntity.ok("Goal marked as completed");
    }




}
