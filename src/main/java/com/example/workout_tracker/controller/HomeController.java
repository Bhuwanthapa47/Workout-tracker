package com.example.workout_tracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }


    @GetMapping("/login")
    public String login() {
        return "login"; // this looks for login.html in templates/
    }


    @GetMapping("/register")
    public String register() {
        return "register";
    }


}
