package com.example.tracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    public enum Status {
        NOT_READY, READY, IN_PROGRESS, UNDER_VALIDATION, COMPLETED
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long weekId;
    
    @Column(nullable = false)
    private String taskName;
    
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.NOT_READY;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}