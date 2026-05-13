package com.example.tracker.service;

import com.example.tracker.model.Week;
import java.util.List;
import java.util.Optional;

public interface WeekService {
    Week createWeek(Week week);
    Optional<Week> getWeekById(Long id);
    List<Week> getWeeksByProjectId(Long projectId);
    Week updateWeek(Long id, Week week);
    void deleteWeek(Long id);
    int getTaskCountForWeek(Long weekId);
    int getCompletedTaskCountForWeek(Long weekId);
}