package com.example.workout_tracker.dto;

import com.example.workout_tracker.model.WorkoutItem;
import java.util.List;

public class WorkoutPlanRequest {
    public Long userId;
    public String date;
    public List<WorkoutItem> items;

    // Getters/setters can be added if needed
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public List<WorkoutItem> getItems() {
        return items;
    }
    public void setItems(List<WorkoutItem> items) {
        this.items = items;
    }
}
