package com.example.tracker.model;

public enum TaskStatus {
    NOT_READY("Not Ready"),
    READY("Ready"),
    IN_PROGRESS("In Progress"),
    UNDER_VALIDATION("Under Validation"),
    COMPLETED("Completed");

    private String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}