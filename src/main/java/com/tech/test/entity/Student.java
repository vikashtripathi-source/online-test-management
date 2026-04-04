package com.tech.test.entity;

import com.tech.test.enums.Branch;
import com.tech.test.enums.StudentRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(
        name = "students",
        uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
@Data
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotNull(message = "Branch cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Branch branch;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "\\d{10}", message = "Mobile number must be exactly 10 digits")
    @Column(name = "mobile_number", nullable = false)
    private String mobileNumber;

    private String imageUrl;

    private String imageFilename;

    @NotNull(message = "Role cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentRole role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private String name;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
