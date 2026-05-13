package com.example.tracker.controller;

import com.example.tracker.model.User;
import com.example.tracker.service.UserService;
import com.example.tracker.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

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

    // Handle Registration (Form)
    @PostMapping("/register")
    public String registerUser(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {
        
        try {
            if (!password.equals(confirmPassword)) {
                model.addAttribute("error", "Passwords do not match!");
                return "register";
            }

            if (userService.getUserByEmail(email).isPresent()) {
                model.addAttribute("error", "Email already exists!");
                return "register";
            }

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

    // ============== REST APIs ==============

    // REST API: Login with JWT Token
    @PostMapping("/api/auth/token")
    @ResponseBody
    public ResponseEntity<?> loginAPI(@RequestBody LoginRequest loginRequest) {
        try {
            var user = userService.getUserByEmail(loginRequest.getEmail());
            
            if (user.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "User not found");
                return ResponseEntity.status(401).body(error);
            }

            User foundUser = user.get();
            
            // Verify password
            if (!passwordEncoder.matches(loginRequest.getPassword(), foundUser.getPassword())) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Invalid password");
                return ResponseEntity.status(401).body(error);
            }

            // Generate JWT token
            String token = jwtUtil.generateToken(foundUser.getEmail());
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("email", foundUser.getEmail());
            response.put("fullName", foundUser.getFullName());
            response.put("message", "Login successful");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }

    // REST API: Register
    @PostMapping("/api/auth/register")
    @ResponseBody
    public ResponseEntity<?> registerUserAPI(@RequestBody User user) {
        try {
            if (userService.getUserByEmail(user.getEmail()).isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Email already exists");
                return ResponseEntity.status(400).body(error);
            }

            User newUser = userService.registerUser(user);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("user", newUser);
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }

    // REST API: Get user by email
    @GetMapping("/api/auth/user/{email}")
    @ResponseBody
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        try {
            var user = userService.getUserByEmail(email);
            return user.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(404).build());
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to fetch user");
            return ResponseEntity.status(400).body(error);
        }
    }
}

// Login Request DTO
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