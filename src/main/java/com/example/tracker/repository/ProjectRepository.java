package com.example.tracker.repository;

import com.example.tracker.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByDeveloperId(Long developerId);
    List<Project> findByProjectName(String projectName);
}