package com.hotelconnect.backend.users;

import lombok.Getter;

@Getter
public class JwtResponse {
    // Getter
    private final String jwt;

    public JwtResponse(String jwt) {
        this.jwt = jwt;
    }

}
