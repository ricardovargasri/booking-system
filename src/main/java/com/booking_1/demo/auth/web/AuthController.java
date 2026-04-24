package com.booking_1.demo.auth.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.booking_1.demo.auth.dtos.AuthResponse;
import com.booking_1.demo.auth.dtos.LoginRequest;
import com.booking_1.demo.auth.dtos.RefreshRequest;
import com.booking_1.demo.auth.services.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Endpoints de autenticación y gestión de sesión")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Devuelve un Access Token (50 min) y un Refresh Token (20 días)")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión", description = "Revoca el Access Token en Redis y el Refresh Token en BD")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody RefreshRequest request) {

        // Extraemos el token del header "Bearer eyJ..."
        String accessToken = authHeader.substring(7);
        authService.logout(accessToken, request.refreshToken());
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar tokens", description = "Intercambia el Refresh Token por un nuevo par de tokens (rotación)")
    public AuthResponse refresh(@RequestBody RefreshRequest request) {
        return authService.refresh(request);
    }
}

