package com.webpage.T3D.outer.dto;

import lombok.Data;

@Data
public class UserRegistrationDto {
    private String username;
    private String email;
    private String password; // Raw password from the form
}