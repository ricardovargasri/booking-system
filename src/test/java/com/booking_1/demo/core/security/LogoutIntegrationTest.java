package com.booking_1.demo.core.security;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import com.booking_1.demo.core.security.jwt.JwtService;
import com.booking_1.demo.core.security.redis.TokenBlacklistService;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver"
})
@AutoConfigureMockMvc
class LogoutIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private TokenBlacklistService tokenBlacklistService;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @MockBean
    private com.booking_1.demo.booking.services.IBookingService bookingService;

    @Test
    @DisplayName("Debe rechazar un token válido si ha sido revocado (Logout)")
    void shouldRejectValidTokenWhenRevoked() throws Exception {
        // 1. Generamos un token válido para un usuario
        UserDetails userDetails = new User("test@example.com", "password", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        
        String token = jwtService.generateAccessToken(userDetails);

        // 2. Simulamos que el token está en la lista negra de Redis
        when(tokenBlacklistService.isRevoked(token)).thenReturn(true);

        // 3. Intentamos acceder a un recurso protegido
        mockMvc.perform(get("/api/v1/bookings/my")
                .header("Authorization", "Bearer " + token))
                // 4. Debe dar 401 aunque el token sea matemáticamente válido
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Debe permitir un token válido si NO ha sido revocado")
    void shouldAllowValidTokenWhenNotRevoked() throws Exception {
        UserDetails userDetails = new User("test@example.com", "password", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        
        String token = jwtService.generateAccessToken(userDetails);

        // Simulamos que NO está en la lista negra
        when(tokenBlacklistService.isRevoked(token)).thenReturn(false);
        
        when(bookingService.FindMyBookings(org.mockito.ArgumentMatchers.any())).thenReturn(org.springframework.data.domain.Page.empty());

        mockMvc.perform(get("/api/v1/bookings/my")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
