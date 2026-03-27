package com.tech.test.entity;

import jakarta.persistence.*;
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

    private String name;

    private String email;

    private String password;

    private Long studentId;

    private String branch;

    @Lob
    @Column(name = "image", columnDefinition = "LONGBLOB")
    private byte[] image;
}
