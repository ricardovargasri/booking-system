package com.booking_1.demo.auth.dtos;

/**
 * Request para el endpoint POST /auth/refresh
 * El cliente envía su Refresh Token para obtener un nuevo par de tokens.
 */
public record RefreshRequest(String refreshToken) {}
