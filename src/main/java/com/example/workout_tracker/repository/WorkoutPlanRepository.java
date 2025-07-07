package com.example.workout_tracker.repository;

import com.example.workout_tracker.model.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {
    List<WorkoutPlan> findByUserId(Long userId);
    List<WorkoutPlan> findByUserIdAndCompletedIsTrueAndDateAfter(Long userId, LocalDate date);
    List<WorkoutPlan> findByUserIdAndCompletedIsTrue(Long userId);




}
