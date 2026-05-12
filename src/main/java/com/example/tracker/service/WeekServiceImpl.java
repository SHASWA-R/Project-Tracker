package com.example.tracker.service;

import com.example.tracker.model.Week;
import com.example.tracker.repository.WeekRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WeekServiceImpl implements WeekService {

    @Autowired
    private WeekRepository weekRepository;

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
        Week existingWeek = weekRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Week not found!"));
        
        existingWeek.setWeekNumber(week.getWeekNumber());
        existingWeek.setStartDate(week.getStartDate());
        existingWeek.setEndDate(week.getEndDate());
        existingWeek.setDescription(week.getDescription());
        existingWeek.setUpdatedAt(LocalDateTime.now());
        return weekRepository.save(existingWeek);
    }

    @Override
    public void deleteWeek(Long id) {
        weekRepository.deleteById(id);
    }

    @Override
    public Optional<Week> getWeekByProjectIdAndWeekNumber(Long projectId, Integer weekNumber) {
        return Optional.ofNullable(weekRepository.findByProjectIdAndWeekNumber(projectId, weekNumber));
    }
}