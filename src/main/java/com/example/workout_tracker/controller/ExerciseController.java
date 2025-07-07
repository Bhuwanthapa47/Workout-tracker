package com.example.workout_tracker.controller;

import com.example.workout_tracker.model.Exercise;
import com.example.workout_tracker.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
@CrossOrigin
public class ExerciseController {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @PostMapping
    public String addExercise(@RequestBody Exercise exercise) {
        exerciseRepository.save(exercise);
        return "Exercise added!";
    }

    @GetMapping
    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public String deleteExercise(@PathVariable Long id) {
        exerciseRepository.deleteById(id);
        return "Exercise deleted!";
    }
}
