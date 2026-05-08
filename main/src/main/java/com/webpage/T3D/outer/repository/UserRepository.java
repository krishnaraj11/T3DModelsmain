package com.webpage.T3D.outer.repository;

import com.webpage.T3D.outer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Custom queries to check if a user already exists during registration
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}