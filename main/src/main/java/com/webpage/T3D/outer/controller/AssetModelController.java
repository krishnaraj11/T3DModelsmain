package com.webpage.T3D.outer.controller;

import com.webpage.T3D.outer.model.AssetModel;
import com.webpage.T3D.outer.model.User;
import com.webpage.T3D.outer.repository.AssetModelRepository;
import com.webpage.T3D.outer.repository.UserRepository;
import com.webpage.T3D.outer.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/models")
@CrossOrigin(origins = "http://localhost:4200")
public class AssetModelController {

    @Autowired
    private AssetModelRepository assetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    // 1. Get the Public Storefront Feed
    @GetMapping("/feed")
    public ResponseEntity<List<AssetModel>> getStorefrontFeed() {
        return ResponseEntity.ok(assetRepository.findByIsPublishedTrue());
    }

    // 2. View a Specific Model Details Page
    @GetMapping("/{id}")
    public ResponseEntity<AssetModel> getModelDetails(@PathVariable Long id) {
        return assetRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- CREATOR HUB ENDPOINTS ---
    // 1. Fetch only the models belonging to the logged-in user
    @GetMapping("/creator/{username}")
    public ResponseEntity<List<AssetModel>> getCreatorModels(@PathVariable String username) {
        List<AssetModel> userModels = assetRepository.findByCreator_Username(username);
        return ResponseEntity.ok(userModels);
    }

    // 2. Delete a specific model by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteModel(@PathVariable Long id) {
        if (assetRepository.existsById(id)) {
            assetRepository.deleteById(id);
            return ResponseEntity.ok("Model deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Model not found.");
        }
    }

    // 🔒 SECURE UPLOADER: We removed @RequestParam("creatorId") entirely!
    @PostMapping("/upload")
    public ResponseEntity<?> uploadModel(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("fileFormat") String fileFormat,
            @RequestParam("polyCount") Integer polyCount,
            @RequestParam("creatorUsername") String creatorUsername,
            @RequestPart("file") MultipartFile file) {

        try {
            User creator = userRepository.findByUsername(creatorUsername)
                    .orElseThrow(() -> new RuntimeException("Creator not found"));

            // 1. Physically save the uploaded file (e.g., SKM_Villager.fbx)
            String savedFileName = fileStorageService.storeFile(file);
            String finalDbFileName = savedFileName;

            // 2. THE INTERCEPTION: If it's an FBX, convert it!
            if (fileFormat.equalsIgnoreCase("FBX")) {
                // Run the engine we just built
                finalDbFileName = convertFbxToGlb(savedFileName);

                // Optional: You could delete the original .fbx here to save hard drive space!
            }

            // 3. Map everything to the Database Entity
            AssetModel newModel = new AssetModel();
            newModel.setCreator(creator);
            newModel.setTitle(title);
            newModel.setDescription(description);
            newModel.setPrice(price);
            newModel.setFileFormat("GLB"); // We force the DB to register it as a web-ready GLB now!
            newModel.setPolyCount(polyCount);

            // 4. Save the new converted URL!
            newModel.setModelFileUrl("/uploads/" + finalDbFileName);
            newModel.setIsPublished(true);

            assetRepository.save(newModel);

            return ResponseEntity.ok("Model '" + title + "' uploaded and processed successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process the 3D model: " + e.getMessage());
        }
    }

    // THE BULLETPROOF CONVERSION ENGINE
    private String convertFbxToGlb(String originalFileName) throws IOException, InterruptedException {
        String baseName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
        String glbFileName = baseName + ".glb";

        // 🚨 THE FIX: Change this string to exactly where your FileStorageService saves files!
        // Based on your screenshot, it is likely "main/uploaded-models/" or just "uploaded-models/"
        String targetFolder = "uploaded-models/";

        File toolFile = new File("main/tools/FBX2glTF-windows-x64.exe");
        File inputFile = new File(targetFolder + originalFileName);
        File outputFile = new File(targetFolder + glbFileName);

        // Safety Check 1: Is the Tool there?
        if (!toolFile.exists()) {
            throw new RuntimeException("CRITICAL: Converter tool missing. Java looked here: " + toolFile.getAbsolutePath());
        }

        // Safety Check 2: Is the FBX file actually there? (This catches the 105 Error!)
        if (!inputFile.exists()) {
            throw new RuntimeException("CRITICAL: The FBX file is missing! Java looked here: " + inputFile.getAbsolutePath());
        }

        System.out.println("⏳ Starting conversion for: " + originalFileName);

        ProcessBuilder processBuilder = new ProcessBuilder(
                toolFile.getAbsolutePath(),
                "-i", inputFile.getAbsolutePath(),
                "-o", outputFile.getAbsolutePath()
        );

        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            System.out.println("✅ Conversion successful: " + glbFileName);
            return glbFileName;
        } else {
            throw new RuntimeException("❌ FBX2glTF failed with exit code: " + exitCode);
        }
    }
}