package com.hotelconnect.backend.users;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;

    // Getters y setters
}

