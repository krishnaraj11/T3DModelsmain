package com.webpage.T3D.outer.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. MUST ADD THIS: Tells security to respect @CrossOrigin annotations
                .cors(Customizer.withDefaults())

                // 1. New Lambda way to disable CSRF
                .csrf(csrf -> csrf.disable())

                // 2. New Lambda way to authorize requests
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/register").permitAll()
                        .requestMatchers("/api/users/login").permitAll()
                        .requestMatchers("/api/users/profile").permitAll()
                        .requestMatchers("/api/users/public/**").permitAll()
                        .requestMatchers("/api/models/feed").permitAll()
                        .requestMatchers("/api/models/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .anyRequest().authenticated()
                )

                // 3. Enable basic auth with defaults
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}