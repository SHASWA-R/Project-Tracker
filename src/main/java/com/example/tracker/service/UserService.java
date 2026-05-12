package com.example.tracker.service;

import com.example.tracker.model.User;
import java.util.Optional;

public interface UserService {
    User registerUser(User user);
    Optional<User> getUserById(Long id);
    Optional<User> getUserByEmail(String email);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
    boolean authenticateUser(String email, String password);
}