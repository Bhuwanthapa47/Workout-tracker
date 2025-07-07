package com.example.workout_tracker.controller;

import org.springframework.ui.Model;
import com.example.workout_tracker.model.WorkoutPlan;
import com.example.workout_tracker.repository.UserRepository;
import com.example.workout_tracker.repository.WorkoutPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private WorkoutPlanRepository workoutPlanRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping({"/", "/dashboard"})
    public String home(Model model) {
        Long testUserId = 1L; // Replace with dynamic user ID later
        List<WorkoutPlan> plans = workoutPlanRepository.findByUserId(testUserId);
        model.addAttribute("workouts", plans);
        return "dashboard";
    }

}



