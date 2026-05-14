package com.booking_1.demo.spot.services;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.booking_1.demo.spot.entities.Spot;
import com.booking_1.demo.spot.repositories.SpotRepository;
import com.booking_1.demo.user.entities.User;
import com.booking_1.demo.spot.dtos.SpotRegistrationDto;

@ExtendWith(MockitoExtension.class)
class SpotServiceSecurityTest {

    @Mock
    private SpotRepository spotRepository;

    @Mock
    private com.booking_1.demo.spot.mappers.SpotMapper spotMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private SpotServiceImpl spotService;

    @Test
    @DisplayName("Debe lanzar una excepción si un usuario intenta editar un Spot que no le pertenece")
    void shouldThrowExceptionWhenUserIsNotOwner() {
        // GIVEN: Un spot que pertenece al Usuario A
        UUID ownerId = UUID.randomUUID();
        User owner = new User();
        owner.setId(ownerId);

        Spot existingSpot = new Spot();
        existingSpot.setId(1L);
        existingSpot.setOwner(owner);

        // GIVEN: Un usuario logueado que es el Usuario B (ID diferente)
        UUID otherUserId = UUID.randomUUID();
        User otherUser = new User();
        otherUser.setId(otherUserId);

        when(spotRepository.findById(1L)).thenReturn(Optional.of(existingSpot));

        // Simulamos que el usuario logueado en Spring Security es el Usuario B
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(otherUser);

        // WHEN & THEN: Al intentar actualizar, debería lanzar AccessDeniedException
        SpotRegistrationDto updateDto = new SpotRegistrationDto("Nuevo Nombre", "Ubicacion", 100.0, 2, null);

        assertThatThrownBy(() -> spotService.updateSpot(1L, updateDto))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("No tienes permiso para editar este alojamiento");
    }

    @Test
    @DisplayName("Debe lanzar una excepción si un usuario intenta eliminar un Spot que no le pertenece")
    void shouldThrowExceptionWhenUserIsNotOwnerOnDelete() {
        // GIVEN: Un spot que pertenece al Usuario A
        UUID ownerId = UUID.randomUUID();
        User owner = new User();
        owner.setId(ownerId);

        Spot existingSpot = new Spot();
        existingSpot.setId(1L);
        existingSpot.setOwner(owner);

        // GIVEN: Un usuario logueado que es el Usuario B
        UUID otherUserId = UUID.randomUUID();
        User otherUser = new User();
        otherUser.setId(otherUserId);

        when(spotRepository.findById(1L)).thenReturn(Optional.of(existingSpot));

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(otherUser);

        // WHEN & THEN: Al intentar eliminar, debería lanzar AccessDeniedException
        assertThatThrownBy(() -> spotService.deleteSpot(1L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("No tienes permiso para eliminar este alojamiento");
    }
}
