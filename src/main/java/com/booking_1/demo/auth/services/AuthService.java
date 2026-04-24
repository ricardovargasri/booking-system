package com.booking_1.demo.auth.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.booking_1.demo.auth.dtos.AuthResponse;
import com.booking_1.demo.auth.dtos.LoginRequest;
import com.booking_1.demo.auth.dtos.RefreshRequest;
import com.booking_1.demo.auth.entities.RefreshToken;
import com.booking_1.demo.auth.repositories.RefreshTokenRepository;
import com.booking_1.demo.core.exceptions.BadRequestException;
import com.booking_1.demo.core.exceptions.ResourceNotFoundException;
import com.booking_1.demo.core.security.jwt.JwtService;
import com.booking_1.demo.core.security.redis.TokenBlacklistService;
import com.booking_1.demo.user.entities.User;
import com.booking_1.demo.user.mappers.UserMapper;
import com.booking_1.demo.user.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenBlacklistService tokenBlacklistService;

    // ------------------------------------------------------------------
    // LOGIN: Valida credenciales y devuelve Access Token + Refresh Token
    // ------------------------------------------------------------------
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // 1. Validar credenciales (lanza excepción si son incorrectas)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        // 2. Buscar el usuario en BD
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 3. Generar Access Token (JWT, 50 min)
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtService.generateAccessToken(userDetails);

        // 4. Revocar tokens de refresco anteriores del usuario (seguridad)
        refreshTokenRepository.revokeAllUserTokens(user);

        // 5. Crear y guardar el nuevo Refresh Token en BD
        RefreshToken refreshToken = RefreshToken.builder()
                .token(jwtService.generateRefreshTokenValue())
                .user(user)
                .expiresAt(jwtService.generateRefreshTokenExpiry())
                .build();
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(accessToken, refreshToken.getToken(), userMapper.toDto(user));
    }

    // ------------------------------------------------------------------
    // LOGOUT: Revoca el Access Token en Redis y el Refresh Token en BD
    // ------------------------------------------------------------------
    @Transactional
    public void logout(String accessToken, String refreshTokenValue) {
        // 1. Meter el Access Token en la lista negra de Redis (con TTL dinámico)
        tokenBlacklistService.revokeToken(accessToken);

        // 2. Marcar el Refresh Token como revocado en BD
        refreshTokenRepository.findByToken(refreshTokenValue)
                .ifPresent(rt -> {
                    rt.setRevoked(true);
                    refreshTokenRepository.save(rt);
                });
    }

    // ------------------------------------------------------------------
    // REFRESH: Valida Refresh Token, devuelve par nuevo (rotación)
    // ------------------------------------------------------------------
    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        // 1. Buscar el Refresh Token en BD
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new BadRequestException("Refresh token inválido"));

        // 2. Verificar que sigue siendo válido (no revocado y no expirado)
        if (!refreshToken.isValid()) {
            throw new BadRequestException("Refresh token expirado o revocado. Por favor inicia sesión nuevamente.");
        }

        // 3. Generar nuevo Access Token
        User user = refreshToken.getUser();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String newAccessToken = jwtService.generateAccessToken(userDetails);

        // 4. ROTACIÓN: Revocar el Refresh Token usado y crear uno nuevo
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        RefreshToken newRefreshToken = RefreshToken.builder()
                .token(jwtService.generateRefreshTokenValue())
                .user(user)
                .expiresAt(jwtService.generateRefreshTokenExpiry())
                .build();
        refreshTokenRepository.save(newRefreshToken);

        return new AuthResponse(newAccessToken, newRefreshToken.getToken(), userMapper.toDto(user));
    }
}

