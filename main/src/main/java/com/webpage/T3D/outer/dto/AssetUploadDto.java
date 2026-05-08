package com.webpage.T3D.outer.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AssetUploadDto {
    private Long creatorId; // Temporarily passed until we add Security/Tokens
    private String title;
    private String description;
    private BigDecimal price;
    private String fileFormat;
    private Integer polyCount;
    // In a real app, you'd handle MultipartFile for actual uploads here
    private String modelFileUrl;
    private Boolean isPublished;
}