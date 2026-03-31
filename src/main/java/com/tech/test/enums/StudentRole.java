package com.tech.test.enums;

public enum StudentRole {
    STUDENT("Student"),
    TEACHER("Teacher"),
    ADMIN("Admin"),
    MANAGER("Manager");

    private String displayName;

    StudentRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
