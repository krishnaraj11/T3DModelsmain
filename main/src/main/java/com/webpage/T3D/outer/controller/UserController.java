package com.webpage.T3D.outer.controller;

import com.webpage.T3D.outer.dto.UserRegistrationDto;
import com.webpage.T3D.outer.model.User;
import com.webpage.T3D.outer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDto dto) {

        // 🚨 ADD THIS NEW CHECK: Reject short passwords from Postman/Hackers
        if (dto.getPassword() == null || dto.getPassword().length() < 8) {
            return ResponseEntity.badRequest().body("Password must be at least 8 characters long.");
        }
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email is already in use.");
        }
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username is taken.");
        }

        User newUser = new User();
        newUser.setUsername(dto.getUsername());
        newUser.setEmail(dto.getEmail());
        // NOTE: We will add BCrypt password hashing later! Never save raw passwords.
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        newUser.setPasswordHash(encodedPassword);

        userRepository.save(newUser);
        return ResponseEntity.ok("User registered successfully!");
    }

    // <-- 2. ADD THIS ENTIRE LOGIN METHOD -->
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials) {
        // Angular sends the input as "username" in the JSON, even if it's an email
        String identifier = credentials.get("username");
        String password = credentials.get("password");

        User user = null;

        // 1. SMART SEARCH: Check if the input contains an "@" symbol
        if (identifier != null && identifier.contains("@")) {
            // It's an email! Search the email column.
            user = userRepository.findByEmail(identifier).orElse(null);
        } else {
            // It's a username! Search the username column.
            user = userRepository.findByUsername(identifier).orElse(null);
        }

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        // 2. Check if the password matches the BCrypt hash
        if (passwordEncoder.matches(password, user.getPasswordHash())) {

            // 3. Success! Return the token
            Map<String, String> response = new HashMap<>();
            response.put("token", "dummy-jwt-token-for-" + user.getUsername());
            // Always return the actual username, even if they logged in with an email
            response.put("username", user.getUsername());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }
    }

    // 🔒 UPDATED: SECURE PROFILE FETCHING
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long id, Principal principal) {
        // 1. Get the username of the person actually logged in
        String loggedInUsername = principal.getName();

        // 2. Fetch the user they are trying to look at
        User requestedUser = userRepository.findById(id).orElse(null);

        if (requestedUser == null) {
            return ResponseEntity.notFound().build();
        }

        // 3. THE SECURITY CHECK: Do the usernames match?
        if (!requestedUser.getUsername().equals(loggedInUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Error 403: Access Denied. You can only view your own profile.");
        }

        return ResponseEntity.ok(requestedUser);
    }
}