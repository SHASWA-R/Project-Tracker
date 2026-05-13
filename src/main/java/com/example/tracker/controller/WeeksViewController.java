package com.example.tracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WeeksViewController {
    
    @GetMapping("/weeks")
    public String weeks() {
        return "weeks";
    }

    @GetMapping("/weeks/{projectId}")
    public String weeksByProject(@PathVariable Long projectId) {
        return "weeks";
    }
}