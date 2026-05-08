package com.webpage.T3D.outer.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Entity

@Table(name = "users")
@Data // This Lombok annotation automatically generates Getters and Setters for us
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String profileImageUrl;

    private String paypalEmail; // For creator payouts

    @Column(updatable = false)
    private Instant createdAt = Instant.now();

    private Instant updatedAt = Instant.now();
}