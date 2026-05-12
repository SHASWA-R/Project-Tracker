package com.example.tracker.service;

import com.example.tracker.model.Project;
import java.util.List;
import java.util.Optional;

public interface ProjectService {
    Project createProject(Project project);
    Optional<Project> getProjectById(Long id);
    List<Project> getAllProjects();
    List<Project> getProjectsByDeveloperId(Long developerId);
    Project updateProject(Long id, Project project);
    void deleteProject(Long id);
}