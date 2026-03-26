package com.tech.test.entity;

import com.tech.test.enums.AddressType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "address")
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String streetAddress;

    private String city;

    private String state;

    private String zipCode;

    private String country;

    @Enumerated(EnumType.STRING)
    private AddressType addressType;

    private String phoneNumber;

    private String email;

    private Long studentId; // Added for report linking
}
