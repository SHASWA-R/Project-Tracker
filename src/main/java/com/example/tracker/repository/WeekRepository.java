package com.example.tracker.repository;

import com.example.tracker.model.Week;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WeekRepository extends JpaRepository<Week, Long> {
    List<Week> findByProjectId(Long projectId);
    Week findByProjectIdAndWeekNumber(Long projectId, Integer weekNumber);
}