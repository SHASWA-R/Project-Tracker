package com.example.tracker.controller;

import com.example.tracker.model.User;
import com.example.tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Show Login Page
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // Show Register Page
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    // Handle Registration
    @PostMapping("/register")
    public String registerUser(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {
        
        try {
            // Check if passwords match
            if (!password.equals(confirmPassword)) {
                model.addAttribute("error", "Passwords do not match!");
                return "register";
            }

            // Check if email already exists
            if (userService.getUserByEmail(email).isPresent()) {
                model.addAttribute("error", "Email already exists!");
                return "register";
            }

            // Create new user
            User user = new User();
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPassword(password);

            userService.registerUser(user);
            model.addAttribute("success", "Account created successfully!");
            return "register";

        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }
    }

    // Show Dashboard
    @GetMapping("/dashboard")
    public String showDashboard() {
        return "dashboard";
    }

    // REST API: Register (for API calls)
    @PostMapping("/api/auth/register")
    @ResponseBody
    public ResponseEntity<User> registerUserAPI(@RequestBody User user) {
        try {
            User newUser = userService.registerUser(user);
            return ResponseEntity.status(201).body(newUser);
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    // REST API: Get user by email
    @GetMapping("/api/auth/user/{email}")
    @ResponseBody
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        try {
            var user = userService.getUserByEmail(email);
            return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(404).build());
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }
}

class LoginRequest {
    private String email;
    private String password;

    public LoginRequest() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}