package com.booking_1.demo.core.security.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.booking_1.demo.core.security.jwt.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    // Spring Data Redis nos da esta clase lista para usar con Strings
    private final StringRedisTemplate redisTemplate;
    private final JwtService jwtService;

    // Prefijo para las claves en Redis (buena práctica para organizar)
    private static final String BLACKLIST_PREFIX = "blacklist:";

    /**
     * Revoca un Access Token metiéndolo en Redis con un TTL igual
     * al tiempo que le queda de vida. Cuando ese tiempo pase,
     * Redis borra la entrada solo, sin que hagamos nada.
     */
    public void revokeToken(String token) {
        long ttlMillis = jwtService.getRemainingTimeMillis(token);

        // Solo metemos el token si le queda tiempo de vida
        // (no tiene sentido meter en la lista un token ya expirado)
        if (ttlMillis > 0) {
            redisTemplate.opsForValue().set(
                BLACKLIST_PREFIX + token,
                "revoked",
                ttlMillis,
                TimeUnit.MILLISECONDS
            );
        }
    }

    /**
     * Consulta si un Access Token está en la lista negra.
     * Esta llamada tarda < 1ms gracias a Redis en memoria.
     */
    public boolean isRevoked(String token) {
        return Boolean.TRUE.equals(
            redisTemplate.hasKey(BLACKLIST_PREFIX + token)
        );
    }
}
