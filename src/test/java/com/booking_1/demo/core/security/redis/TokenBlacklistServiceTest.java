package com.booking_1.demo.core.security.redis;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.booking_1.demo.core.security.jwt.JwtService;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private TokenBlacklistService tokenBlacklistService;

    @Test
    @DisplayName("Debe guardar el token en Redis con el TTL correcto")
    void shouldSaveTokenInRedisWithCorrectTTL() {
        String token = "test-token";
        long ttl = 100000L; // 100 segundos

        when(jwtService.getRemainingTimeMillis(token)).thenReturn(ttl);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        tokenBlacklistService.revokeToken(token);

        verify(valueOperations).set(
                eq("blacklist:" + token),
                eq("revoked"),
                eq(ttl),
                eq(TimeUnit.MILLISECONDS)
        );
    }

    @Test
    @DisplayName("No debe guardar el token si ya ha expirado")
    void shouldNotSaveTokenIfAlreadyExpired() {
        String token = "expired-token";
        
        when(jwtService.getRemainingTimeMillis(token)).thenReturn(0L);

        tokenBlacklistService.revokeToken(token);

        verify(redisTemplate, never()).opsForValue();
    }
}
