package com.webpage.T3D.outer.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "models")
@Data
public class AssetModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // This links the 3D model back to the User who created it
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // BigDecimal is best for money/prices in Java
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(nullable = false)
    private String modelFileUrl; // Where the .obj/.fbx file is stored

    private String textureFileUrl;

    private String thumbnailUrl;

    private Integer polyCount;

    @Column(length = 20)
    private String fileFormat;

    private Boolean isPublished = false;

    @Column(updatable = false)
    private Instant createdAt = Instant.now();

    private Instant updatedAt = Instant.now();
}