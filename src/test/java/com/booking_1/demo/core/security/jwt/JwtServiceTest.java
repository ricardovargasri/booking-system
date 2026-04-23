package com.booking_1.demo.core.security.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

public class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        // En TDD, aquí verás que JwtService no existe, por eso marcará error.
        jwtService = new JwtService();

        userDetails = new User("testuser", "password", new ArrayList<>());
    }

    @Test
    void generateToken_ShouldReturnNotNullToken() {
        // Intentamos generar un token
        String token = jwtService.generateToken(userDetails);

        // La prueba fallará aquí porque el método no existe o retornará algo inválido
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        // Primero generamos un token válido
        String token = jwtService.generateToken(userDetails);

        // Intentamos extraer el username (este método aún no existe o no tiene lógica)
        String extractedUsername = jwtService.extractUsername(token);

        // Verificamos que sea igual al original "testuser"
        assertEquals("testuser", extractedUsername);
    }
}
