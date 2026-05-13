package com.example.tracker.controller;

import com.example.tracker.model.Task;
import com.example.tracker.model.User;
import com.example.tracker.model.Week;
import com.example.tracker.model.Project;
import com.example.tracker.service.TaskService;
import com.example.tracker.service.UserService;
import com.example.tracker.repository.TaskRepository;
import com.example.tracker.repository.WeekRepository;
import com.example.tracker.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private WeekRepository weekRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserService userService;

    // GET tasks by week (FOR CURRENT USER ONLY)
    @GetMapping("/week/{weekId}")
    public ResponseEntity<List<Task>> getTasksByWeek(
            @PathVariable Long weekId,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        try {
            String email = user.getUsername();
            Optional<User> currentUserOpt = userService.getUserByEmail(email);
            
            if (!currentUserOpt.isPresent()) {
                return ResponseEntity.status(401).build();
            }
            
            User currentUser = currentUserOpt.get();
            
            // Verify week belongs to current user's project
            Optional<Week> week = weekRepository.findById(weekId);
            if (!week.isPresent()) {
                return ResponseEntity.status(404).build();
            }
            
            Optional<Project> project = projectRepository.findById(week.get().getProjectId());
            if (!project.isPresent() || !project.get().getDeveloperId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).build(); // Unauthorized
            }
            
            List<Task> tasks = taskRepository.findByWeekId(weekId);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // GET task by ID
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(
            @PathVariable Long id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        try {
            String email = user.getUsername();
            Optional<User> currentUserOpt = userService.getUserByEmail(email);
            
            if (!currentUserOpt.isPresent()) {
                return ResponseEntity.status(401).build();
            }
            
            User currentUser = currentUserOpt.get();
            
            Optional<Task> taskOpt = taskRepository.findById(id);
            if (!taskOpt.isPresent()) {
                return ResponseEntity.status(404).build();
            }
            
            Task task = taskOpt.get();
            Optional<Week> week = weekRepository.findById(task.getWeekId());
            Optional<Project> project = projectRepository.findById(week.get().getProjectId());
            
            if (!project.isPresent() || !project.get().getDeveloperId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).build();
            }
            
            return ResponseEntity.ok(task);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // CREATE new task
    @PostMapping
    public ResponseEntity<?> createTask(
            @RequestBody Task task,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        try {
            String email = user.getUsername();
            Optional<User> currentUserOpt = userService.getUserByEmail(email);
            
            if (!currentUserOpt.isPresent()) {
                return ResponseEntity.status(401).build();
            }
            
            User currentUser = currentUserOpt.get();
            
            // Verify week belongs to current user's project
            Optional<Week> week = weekRepository.findById(task.getWeekId());
            if (!week.isPresent()) {
                return ResponseEntity.status(404).build();
            }
            
            Optional<Project> project = projectRepository.findById(week.get().getProjectId());
            if (!project.isPresent() || !project.get().getDeveloperId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).build();
            }
            
            Task newTask = taskService.createTask(task);
            return ResponseEntity.status(201).body(newTask);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to create task: " + e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }

    // UPDATE task status
    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<?> updateTaskStatus(
            @PathVariable Long id,
            @PathVariable String status,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        try {
            String email = user.getUsername();
            Optional<User> currentUserOpt = userService.getUserByEmail(email);
            
            if (!currentUserOpt.isPresent()) {
                return ResponseEntity.status(401).build();
            }
            
            User currentUser = currentUserOpt.get();
            
            Optional<Task> taskOpt = taskRepository.findById(id);
            if (!taskOpt.isPresent()) {
                return ResponseEntity.status(404).build();
            }
            
            Task task = taskOpt.get();
            Optional<Week> week = weekRepository.findById(task.getWeekId());
            Optional<Project> project = projectRepository.findById(week.get().getProjectId());
            
            if (!project.isPresent() || !project.get().getDeveloperId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).build();
            }
            
            Task updatedTask = taskService.updateTaskStatus(id, Task.Status.valueOf(status));
            return ResponseEntity.ok(updatedTask);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to update task: " + e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }

    // DELETE task
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        try {
            String email = user.getUsername();
            Optional<User> currentUserOpt = userService.getUserByEmail(email);
            
            if (!currentUserOpt.isPresent()) {
                return ResponseEntity.status(401).build();
            }
            
            User currentUser = currentUserOpt.get();
            
            Optional<Task> taskOpt = taskRepository.findById(id);
            if (!taskOpt.isPresent()) {
                return ResponseEntity.status(404).build();
            }
            
            Task task = taskOpt.get();
            Optional<Week> week = weekRepository.findById(task.getWeekId());
            Optional<Project> project = projectRepository.findById(week.get().getProjectId());
            
            if (!project.isPresent() || !project.get().getDeveloperId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).build();
            }
            
            taskService.deleteTask(id);
            return ResponseEntity.ok("Task deleted successfully");
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to delete task: " + e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }
}