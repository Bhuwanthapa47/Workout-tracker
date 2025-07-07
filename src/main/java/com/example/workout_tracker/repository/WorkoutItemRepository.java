package com.example.workout_tracker.repository;

import com.example.workout_tracker.model.WorkoutItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutItemRepository extends JpaRepository<WorkoutItem, Long> {
}
