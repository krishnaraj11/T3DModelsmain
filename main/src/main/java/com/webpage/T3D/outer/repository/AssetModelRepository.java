package com.webpage.T3D.outer.repository;

import com.webpage.T3D.outer.model.AssetModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetModelRepository extends JpaRepository<AssetModel, Long> {
    // Fetch all models created by a specific user (for their Creator Dashboard)
    List<AssetModel> findByCreatorId(Long creatorId);

    // Fetch only published models (for the public storefront feed)
    List<AssetModel> findByIsPublishedTrue();

    // 🚨 ADD THIS: Spring Boot will automatically write the SQL query to find models by the creator's username!
    List<AssetModel> findByCreator_Username(String username);
}