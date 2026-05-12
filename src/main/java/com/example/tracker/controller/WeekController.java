package com.example.tracker.controller;

import com.example.tracker.model.Week;
import com.example.tracker.service.WeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/weeks")
public class WeekController {

    @Autowired
    private WeekService weekService;

    @PostMapping
    public ResponseEntity<Week> createWeek(@RequestBody Week week) {
        try {
            Week newWeek = weekService.createWeek(week);
            return ResponseEntity.status(201).body(newWeek);
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Week> getWeekById(@PathVariable Long id) {
        try {
            var week = weekService.getWeekById(id);
            return week.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(404).build());
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Week>> getWeeksByProjectId(@PathVariable Long projectId) {
        try {
            List<Week> weeks = weekService.getWeeksByProjectId(projectId);
            return ResponseEntity.ok(weeks);
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Week> updateWeek(@PathVariable Long id, @RequestBody Week week) {
        try {
            Week updatedWeek = weekService.updateWeek(id, week);
            return ResponseEntity.ok(updatedWeek);
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWeek(@PathVariable Long id) {
        try {
            weekService.deleteWeek(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/project/{projectId}/week/{weekNumber}")
    public ResponseEntity<Week> getWeekByProjectIdAndWeekNumber(@PathVariable Long projectId, @PathVariable Integer weekNumber) {
        try {
            var week = weekService.getWeekByProjectIdAndWeekNumber(projectId, weekNumber);
            return week.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(404).build());
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }
}