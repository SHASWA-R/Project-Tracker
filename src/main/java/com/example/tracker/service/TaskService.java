package com.example.tracker.service;

import com.example.tracker.model.Task;
import java.util.List;
import java.util.Optional;

public interface TaskService {
    Task createTask(Task task);
    Optional<Task> getTaskById(Long id);
    List<Task> getTasksByWeekId(Long weekId);
    List<Task> getTasksByWeekIdAndStatus(Long weekId, Task.Status status);
    Task updateTask(Long id, Task task);
    void deleteTask(Long id);
    Task updateTaskStatus(Long taskId, Task.Status status);
}