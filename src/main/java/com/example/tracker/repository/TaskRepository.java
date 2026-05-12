package com.example.tracker.repository;

import com.example.tracker.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByWeekId(Long weekId);
    List<Task> findByWeekIdAndStatus(Long weekId, Task.Status status);
}