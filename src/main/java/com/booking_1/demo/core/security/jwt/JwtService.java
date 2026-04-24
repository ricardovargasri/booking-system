package com.booking_1.demo.core.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {

    // En producción esto debe venir de una variable de entorno segura.
    private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Access Token: 50 minutos
    private static final long ACCESS_TOKEN_EXPIRATION = 1000L * 60 * 50;

    // ------------------------------------------------------------------
    // ACCESS TOKEN (JWT firmado, corta duración)
    // ------------------------------------------------------------------

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Añadimos el rol al payload para no consultar la BD en cada petición
        claims.put("roles", userDetails.getAuthorities());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(KEY)
                .compact();
    }

    // Mantener compatibilidad con código existente que llame a generateToken()
    public String generateToken(UserDetails userDetails) {
        return generateAccessToken(userDetails);
    }

    // ------------------------------------------------------------------
    // REFRESH TOKEN (UUID aleatorio, larga duración, vive en BD)
    // ------------------------------------------------------------------

    // Genera el valor del token (un UUID imposible de adivinar)
    public String generateRefreshTokenValue() {
        return UUID.randomUUID().toString();
    }

    // Devuelve cuándo expira: ahora + 20 días
    public LocalDateTime generateRefreshTokenExpiry() {
        return LocalDateTime.now().plusDays(20);
    }

    // ------------------------------------------------------------------
    // UTILIDADES PARA REDIS (Lista Negra de Access Tokens)
    // ------------------------------------------------------------------

    // Devuelve los milisegundos que le quedan de vida al token
    // Usado para poner el TTL exacto en Redis al hacer logout
    public long getRemainingTimeMillis(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        long remaining = expiration.getTime() - System.currentTimeMillis();
        return Math.max(remaining, 0); // Nunca negativo
    }

    // ------------------------------------------------------------------
    // MÉTODOS DE VALIDACIÓN Y EXTRACCIÓN
    // ------------------------------------------------------------------

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

