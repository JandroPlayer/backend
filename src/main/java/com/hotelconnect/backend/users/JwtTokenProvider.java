package com.hotelconnect.backend.users;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final String JWT_SECRET = "mysecretkeymysecretkeymysecretkeymysecretkey"; // Mínimo 256 bits
    private static final long JWT_EXPIRATION = 86400000; // 24 horas

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Genera el token JWT
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // Se guarda el username en el token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Firmamos con una clave segura
                .compact();
    }

    // Valida el token
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(getSigningKey())  // Usamos una clave más segura
                    .build()
                    .parseClaimsJws(token); // Si esto no lanza excepción, el token es válido
            return true;
        } catch (Exception e) {
            return false; // Token inválido o expirado
        }
    }

    // Obtiene el username del token
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // Método para extraer el token desde el encabezado Authorization
    public String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization"); // Obtener el encabezado Authorization
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Quitar "Bearer " y devolver solo el token
        }
        return null; // Si no hay token, devolver null
    }

}
