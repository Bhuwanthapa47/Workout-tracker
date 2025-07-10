package com.example.workout_tracker.controller;

import com.example.workout_tracker.dto.WorkoutPlanRequest;
import com.example.workout_tracker.model.*;
import com.example.workout_tracker.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workout")
@CrossOrigin
public class WorkoutPlanController {

    @Autowired
    private WorkoutPlanRepository workoutPlanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;



    @PostMapping("/create")
    public String createWorkoutPlan(@RequestBody WorkoutPlanRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow();
        WorkoutPlan plan = new WorkoutPlan();
        plan.setUser(user);
        plan.setDate(LocalDate.parse(request.getDate()));

        for (WorkoutItem item : request.getItems()) {
            Exercise exercise = exerciseRepository.findById(item.getExercise().getId()).orElseThrow();
            item.setExercise(exercise);
            item.setWorkoutPlan(plan);
        }

        plan.setItems(request.getItems());
        workoutPlanRepository.save(plan);

        return "Workout Plan created!";
    }


    @GetMapping("/user/{userId}")
    public List<WorkoutPlan> getPlansForUser(@PathVariable Long userId) {
        return workoutPlanRepository.findByUserId(userId);
    }

    @DeleteMapping("/{planId}")
    public String deletePlan(@PathVariable Long planId) {
        workoutPlanRepository.deleteById(planId);
        return "Workout Plan deleted!";
    }

    @PostMapping("/complete/{planId}")
    public String completeWorkout(
            @PathVariable Long planId,
            @RequestBody List<WorkoutItem> actualItems) {

        WorkoutPlan plan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Workout Plan not found"));

        if (plan.isCompleted()) {
            return "Workout already completed!";
        }

        // mark plan as complete
        plan.setCompleted(true);

        // update actuals for each item
        List<WorkoutItem> items = plan.getItems();
        for (int i = 0; i < items.size(); i++) {
            WorkoutItem plannedItem = items.get(i);
            WorkoutItem actualItem = actualItems.get(i);

            plannedItem.setActualSets(actualItem.getActualSets());
            plannedItem.setActualReps(actualItem.getActualReps());
            plannedItem.setActualWeight(actualItem.getActualWeight());
        }

        workoutPlanRepository.save(plan);
        return "Workout marked as complete!";
    }

    @GetMapping("/stats/weekly/{userId}")
    public Map<String, Object> getWeeklyStats(@PathVariable Long userId) {
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);

        List<WorkoutPlan> plans = workoutPlanRepository
                .findByUserIdAndCompletedIsTrueAndDateAfter(userId, sevenDaysAgo);

        int workoutsCompleted = plans.size();
        double totalWeightLifted = 0;

        for (WorkoutPlan plan : plans) {
            for (WorkoutItem item : plan.getItems()) {
                if (item.getActualSets() != null && item.getActualReps() != null && item.getActualWeight() != null) {
                    totalWeightLifted += item.getActualWeight();
                }
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("workoutsCompleted", workoutsCompleted);
        stats.put("totalWeightLifted", totalWeightLifted);

        return stats;
    }

    @GetMapping("/stats/best/{userId}")
    public Map<String, Double> getBestLifts(@PathVariable Long userId) {
        List<WorkoutPlan> plans = workoutPlanRepository.findByUserIdAndCompletedIsTrue(userId);

        Map<String, Double> bestLifts = new HashMap<>();

        for (WorkoutPlan plan : plans) {
            for (WorkoutItem item : plan.getItems()) {
                if (item.getActualWeight() != null) {
                    String exerciseName = item.getExercise().getName();
                    double weight = item.getActualWeight();

                    bestLifts.merge(exerciseName, weight, Math::max);
                }
            }
        }

        return bestLifts;
    }

    @GetMapping("/stats/favorite/{userId}")
    public String getMostFrequentExercise(@PathVariable Long userId) {
        List<WorkoutPlan> plans = workoutPlanRepository.findByUserIdAndCompletedIsTrue(userId);

        Map<String, Integer> exerciseCounts = new HashMap<>();

        for (WorkoutPlan plan : plans) {
            for (WorkoutItem item : plan.getItems()) {
                String exerciseName = item.getExercise().getName();
                exerciseCounts.put(exerciseName, exerciseCounts.getOrDefault(exerciseName, 0) + 1);
            }
        }

        String favorite = exerciseCounts.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No exercises logged yet");

        return favorite;
    }

    @GetMapping("/stats/monthly/{userId}")
    public Map<String, Map<String, Object>> getMonthlyStats(@PathVariable Long userId) {
        List<WorkoutPlan> plans = workoutPlanRepository.findByUserIdAndCompletedIsTrue(userId);

        Map<String, Map<String, Object>> monthlyStats = new TreeMap<>(); // keeps months in order

        for (WorkoutPlan plan : plans) {
            String month = plan.getDate().getYear() + "-" + String.format("%02d", plan.getDate().getMonthValue());

            monthlyStats.putIfAbsent(month, new HashMap<>());
            Map<String, Object> stats = monthlyStats.get(month);

            // increment workouts count
            stats.put("workoutsCompleted", (int) stats.getOrDefault("workoutsCompleted", 0) + 1);

            // add weight lifted
            double totalWeight = (double) stats.getOrDefault("totalWeightLifted", 0.0);

            for (WorkoutItem item : plan.getItems()) {
                if (item.getActualSets() != null && item.getActualReps() != null && item.getActualWeight() != null) {
                    totalWeight += item.getActualSets() * item.getActualReps() * item.getActualWeight();
                }
            }

            stats.put("totalWeightLifted", totalWeight);
        }

        return monthlyStats;
    }

    @GetMapping("/user/{userId}/filter")
    public List<WorkoutPlan> filterPlans(
            @PathVariable Long userId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) Long exerciseId
    ) {
        LocalDate fromDate = (from != null && !from.isEmpty()) ? LocalDate.parse(from) : null;
        LocalDate toDate = (to != null && !to.isEmpty()) ? LocalDate.parse(to) : null;

        List<WorkoutPlan> plans = workoutPlanRepository.findByUserIdAndCompletedIsTrue(userId);

        return plans.stream()
                .filter(plan -> {
                    boolean dateMatch = true;
                    if (fromDate != null) dateMatch &= !plan.getDate().isBefore(fromDate);
                    if (toDate != null) dateMatch &= !plan.getDate().isAfter(toDate);
                    boolean exerciseMatch = (exerciseId == null || exerciseId == 0) ||
                            plan.getItems().stream().anyMatch(i -> i.getExercise().getId().equals(exerciseId));
                    return dateMatch && exerciseMatch;
                })
                .collect(Collectors.toList());
    }




}
