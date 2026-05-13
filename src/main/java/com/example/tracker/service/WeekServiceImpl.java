package com.example.tracker.service;

import com.example.tracker.model.Week;
import com.example.tracker.model.Task;
import com.example.tracker.repository.WeekRepository;
import com.example.tracker.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WeekServiceImpl implements WeekService {

    @Autowired
    private WeekRepository weekRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public Week createWeek(Week week) {
        week.setCreatedAt(LocalDateTime.now());
        week.setUpdatedAt(LocalDateTime.now());
        return weekRepository.save(week);
    }

    @Override
    public Optional<Week> getWeekById(Long id) {
        return weekRepository.findById(id);
    }

    @Override
    public List<Week> getWeeksByProjectId(Long projectId) {
        return weekRepository.findByProjectId(projectId);
    }

    @Override
    public Week updateWeek(Long id, Week week) {
        Optional<Week> existingWeek = weekRepository.findById(id);
        if (existingWeek.isPresent()) {
            Week w = existingWeek.get();
            w.setWeekNumber(week.getWeekNumber());
            w.setStartDate(week.getStartDate());
            w.setEndDate(week.getEndDate());
            w.setUpdatedAt(LocalDateTime.now());
            return weekRepository.save(w);
        }
        return null;
    }

    @Override
    public void deleteWeek(Long id) {
        weekRepository.deleteById(id);
    }

    @Override
    public int getTaskCountForWeek(Long weekId) {
        List<Task> tasks = taskRepository.findByWeekId(weekId);
        return tasks.size();
    }

    @Override
    public int getCompletedTaskCountForWeek(Long weekId) {
        List<Task> tasks = taskRepository.findByWeekId(weekId);
        return (int) tasks.stream()
                .filter(t -> t.getStatus() == Task.Status.COMPLETED)
                .count();
    }
}