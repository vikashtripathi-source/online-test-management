package com.tech.test.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tests")
@Data
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String testName;

    private String branch;

    private int durationMinutes;
}
