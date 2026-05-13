package com.example.tracker.controller;

import com.example.tracker.model.Week;
import com.example.tracker.model.Task;
import com.example.tracker.model.User;
import com.example.tracker.model.Project;
import com.example.tracker.service.WeekService;
import com.example.tracker.service.UserService;
import com.example.tracker.repository.WeekRepository;
import com.example.tracker.repository.TaskRepository;
import com.example.tracker.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/weeks")
public class WeekController {
    
    @Autowired
    private WeekService weekService;

    @Autowired
    private WeekRepository weekRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    // GET weeks by project (verify user owns project)
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Week>> getWeeksByProject(
            @PathVariable Long projectId,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        try {
            String email = user.getUsername();
            Optional<User> currentUserOpt = userService.getUserByEmail(email);
            
            if (!currentUserOpt.isPresent()) {
                return ResponseEntity.status(401).build();
            }
            
            User currentUser = currentUserOpt.get();
            
            // Verify project belongs to current user
            Optional<Project> project = projectRepository.findById(projectId);
            if (!project.isPresent() || !project.get().getDeveloperId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).build(); // Unauthorized
            }
            
            List<Week> weeks = weekService.getWeeksByProjectId(projectId);
            return ResponseEntity.ok(weeks);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // GET weeks with task counts (FOR CURRENT USER ONLY)
    @GetMapping("/project/{projectId}/with-counts")
    public ResponseEntity<List<Map<String, Object>>> getWeeksWithCounts(
            @PathVariable Long projectId,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        try {
            String email = user.getUsername();
            Optional<User> currentUserOpt = userService.getUserByEmail(email);
            
            if (!currentUserOpt.isPresent()) {
                return ResponseEntity.status(401).build();
            }
            
            User currentUser = currentUserOpt.get();
            
            // Verify project belongs to current user
            Optional<Project> projectOpt = projectRepository.findById(projectId);
            if (!projectOpt.isPresent() || !projectOpt.get().getDeveloperId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).build(); // Unauthorized
            }
            
            List<Week> weeks = weekService.getWeeksByProjectId(projectId);
            List<Map<String, Object>> result = weeks.stream().map(week -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", week.getId());
                map.put("weekNumber", week.getWeekNumber());
                map.put("startDate", week.getStartDate());
                map.put("endDate", week.getEndDate());
                map.put("projectId", week.getProjectId());
                map.put("taskCount", weekService.getTaskCountForWeek(week.getId()));
                map.put("completedCount", weekService.getCompletedTaskCountForWeek(week.getId()));
                return map;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // GET week by ID
    @GetMapping("/{id}")
    public ResponseEntity<Week> getWeekById(
            @PathVariable Long id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        try {
            String email = user.getUsername();
            Optional<User> currentUserOpt = userService.getUserByEmail(email);
            
            if (!currentUserOpt.isPresent()) {
                return ResponseEntity.status(401).build();
            }
            
            User currentUser = currentUserOpt.get();
            
            Optional<Week> weekOptional = weekService.getWeekById(id);
            if (weekOptional.isPresent()) {
                Week week = weekOptional.get();
                // Verify project belongs to current user
                Optional<Project> project = projectRepository.findById(week.getProjectId());
                if (!project.isPresent() || !project.get().getDeveloperId().equals(currentUser.getId())) {
                    return ResponseEntity.status(403).build();
                }
                return ResponseEntity.ok(week);
            }
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // CREATE new week
    @PostMapping
    public ResponseEntity<?> createWeek(
            @RequestBody Week week,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        try {
            String email = user.getUsername();
            Optional<User> currentUserOpt = userService.getUserByEmail(email);
            
            if (!currentUserOpt.isPresent()) {
                return ResponseEntity.status(401).build();
            }
            
            User currentUser = currentUserOpt.get();
            
            // Verify project belongs to current user
            Optional<Project> project = projectRepository.findById(week.getProjectId());
            if (!project.isPresent() || !project.get().getDeveloperId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).build();
            }
            
            Week newWeek = weekService.createWeek(week);
            return ResponseEntity.status(201).body(newWeek);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to create week: " + e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }

    // UPDATE week
    @PutMapping("/{id}")
    public ResponseEntity<?> updateWeek(
            @PathVariable Long id,
            @RequestBody Week week,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        try {
            String email = user.getUsername();
            Optional<User> currentUserOpt = userService.getUserByEmail(email);
            
            if (!currentUserOpt.isPresent()) {
                return ResponseEntity.status(401).build();
            }
            
            User currentUser = currentUserOpt.get();
            
            Optional<Week> existingWeek = weekService.getWeekById(id);
            if (!existingWeek.isPresent()) {
                return ResponseEntity.status(404).build();
            }
            
            // Verify project belongs to current user
            Optional<Project> project = projectRepository.findById(existingWeek.get().getProjectId());
            if (!project.isPresent() || !project.get().getDeveloperId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).build();
            }
            
            Week updatedWeek = weekService.updateWeek(id, week);
            return ResponseEntity.ok(updatedWeek);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to update week: " + e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }

    // DELETE week
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWeek(
            @PathVariable Long id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        try {
            String email = user.getUsername();
            Optional<User> currentUserOpt = userService.getUserByEmail(email);
            
            if (!currentUserOpt.isPresent()) {
                return ResponseEntity.status(401).build();
            }
            
            User currentUser = currentUserOpt.get();
            
            Optional<Week> week = weekService.getWeekById(id);
            if (!week.isPresent()) {
                return ResponseEntity.status(404).build();
            }
            
            // Verify project belongs to current user
            Optional<Project> project = projectRepository.findById(week.get().getProjectId());
            if (!project.isPresent() || !project.get().getDeveloperId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).build();
            }
            
            weekService.deleteWeek(id);
            return ResponseEntity.ok("Week deleted successfully");
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to delete week: " + e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }
}