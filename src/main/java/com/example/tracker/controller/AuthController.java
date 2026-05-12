package com.example.tracker.controller;

import com.example.tracker.model.User;
import com.example.tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        try {
            User newUser = userService.registerUser(user);
            return ResponseEntity.status(201).body(newUser);
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequest request) {
        try {
            boolean isAuthenticated = userService.authenticateUser(request.getEmail(), request.getPassword());
            if (isAuthenticated) {
                return ResponseEntity.ok("Login successful");
            } else {
                return ResponseEntity.status(401).body("Invalid credentials");
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    @GetMapping("/user/{email}")
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