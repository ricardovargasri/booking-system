package com.booking_1.demo.auth.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.booking_1.demo.auth.entities.RefreshToken;
import com.booking_1.demo.user.entities.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // Buscar un token por su valor (para validar en el endpoint /refresh)
    Optional<RefreshToken> findByToken(String token);

    // Revocar todos los tokens activos de un usuario (usado en logout)
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user = :user AND rt.revoked = false")
    void revokeAllUserTokens(User user);
}
