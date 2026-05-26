package com.booking_1.demo.core.security.services;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.booking_1.demo.user.entities.User;
import com.booking_1.demo.user.repositories.UserRepository;
import com.booking_1.demo.core.enums.Rol;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final UserRepository userRepository;

    /**
     * Obtiene el usuario autenticado actual.
     * Si el usuario no existe en la base de datos local (pero tiene un token válido),
     * lo crea automáticamente (Just-In-Time Provisioning).
     */
    @Transactional
    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof Jwt jwt)) {
            throw new org.springframework.security.authentication.BadCredentialsException("Token no válido o no presente");
        }

        final String email = jwt.getClaimAsString("email");
        final String name = jwt.getClaimAsString("name") != null 
                ? jwt.getClaimAsString("name") 
                : jwt.getClaimAsString("preferred_username");

        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    // Autocreación (Just-In-Time Provisioning)
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setRol(Rol.USER); // Rol por defecto
                    return userRepository.save(newUser);
                });
    }
}
