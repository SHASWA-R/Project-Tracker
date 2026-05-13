package com.example.tracker.controller;

import com.example.tracker.model.Project;
import com.example.tracker.model.User;
import com.example.tracker.repository.ProjectRepository;
import com.example.tracker.service.ProjectService;
import com.example.tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserService userService;

    // GET all projects for CURRENT USER ONLY
    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        try {
            // Get current logged-in user
            String email = user.getUsername();
            Optional<User> currentUserOpt = userService.getUserByEmail(email);
            
            if (!currentUserOpt.isPresent()) {
                return ResponseEntity.status(401).build();
            }
            
            User currentUser = currentUserOpt.get();
            
            // Get only projects for THIS user
            List<Project> projects = projectRepository.findByDeveloperId(currentUser.getId());
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // GET project by ID (verify ownership)
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(
            @PathVariable Long id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        try {
            String email = user.getUsername();
            Optional<User> currentUserOpt = userService.getUserByEmail(email);
            
            if (!currentUserOpt.isPresent()) {
                return ResponseEntity.status(401).build();
            }
            
            User currentUser = currentUserOpt.get();
            
            Optional<Project> project = projectRepository.findById(id);
            if (!project.isPresent() || !project.get().getDeveloperId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).build(); // Unauthorized
            }
            return ResponseEntity.ok(project.get());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // CREATE new project for current user
    @PostMapping
    public ResponseEntity<?> createProject(
            @RequestBody Project project,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        try {
            String email = user.getUsername();
            Optional<User> currentUserOpt = userService.getUserByEmail(email);
            
            if (!currentUserOpt.isPresent()) {
                return ResponseEntity.status(401).build();
            }
            
            User currentUser = currentUserOpt.get();
            
            // Set the developer ID to current user
            project.setDeveloperId(currentUser.getId());
            
            Project newProject = projectService.createProject(project);
            return ResponseEntity.status(201).body(newProject);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to create project: " + e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }

    // UPDATE project (verify ownership)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(
            @PathVariable Long id,
            @RequestBody Project project,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        try {
            String email = user.getUsername();
            Optional<User> currentUserOpt = userService.getUserByEmail(email);
            
            if (!currentUserOpt.isPresent()) {
                return ResponseEntity.status(401).build();
            }
            
            User currentUser = currentUserOpt.get();
            
            Optional<Project> existingProject = projectRepository.findById(id);
            if (!existingProject.isPresent() || !existingProject.get().getDeveloperId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).build(); // Unauthorized
            }
            
            Project updatedProject = projectService.updateProject(id, project);
            return ResponseEntity.ok(updatedProject);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to update project: " + e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }

    // DELETE project (verify ownership)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(
            @PathVariable Long id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        try {
            String email = user.getUsername();
            Optional<User> currentUserOpt = userService.getUserByEmail(email);
            
            if (!currentUserOpt.isPresent()) {
                return ResponseEntity.status(401).build();
            }
            
            User currentUser = currentUserOpt.get();
            
            Optional<Project> project = projectRepository.findById(id);
            if (!project.isPresent() || !project.get().getDeveloperId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).build(); // Unauthorized
            }
            
            projectService.deleteProject(id);
            return ResponseEntity.ok("Project deleted successfully");
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to delete project: " + e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }
}