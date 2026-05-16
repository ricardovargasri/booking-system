package com.booking_1.demo.spot.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.booking_1.demo.core.exceptions.ResourceNotFoundException;
import com.booking_1.demo.core.security.jwt.JwtService;
import com.booking_1.demo.spot.repositories.SpotRepository;
import com.booking_1.demo.user.entities.User;
import com.booking_1.demo.spot.dtos.SpotCreationResponse;
import com.booking_1.demo.spot.dtos.SpotDto;
import com.booking_1.demo.spot.dtos.SpotRegistrationDto;
import com.booking_1.demo.spot.entities.Spot;
import com.booking_1.demo.spot.mappers.SpotMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SpotServiceImpl implements ISpotService {
    private final SpotMapper spotMapper;
    private final SpotRepository spotRepository;
    private final com.booking_1.demo.user.repositories.UserRepository userRepository;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public SpotCreationResponse save(SpotRegistrationDto spotRegistrationDto) {
        User owner = getCurrentUser();
        String updatedToken = null;

        // Si el usuario es USER, lo promovemos a OWNER al crear su primer spot
        if (owner.getRol() == com.booking_1.demo.core.enums.Rol.USER) {
            owner.setRol(com.booking_1.demo.core.enums.Rol.OWNER);
            userRepository.save(owner);

            // Generamos un nuevo token con el rol actualizado
            UserDetails userDetails = userDetailsService.loadUserByUsername(owner.getEmail());
            updatedToken = jwtService.generateAccessToken(userDetails);
        }

        Spot spot = spotMapper.spotToEntity(spotRegistrationDto);
        spot.setOwner(owner);
        spot.setIsAvailable(true); // Evitamos un NullPointerException al reservar

        Spot spotSaved = spotRepository.save(spot);
        SpotDto spotDto = spotMapper.toDto(spotSaved);

        return new SpotCreationResponse(spotDto, updatedToken);
    }

    @Override
    public SpotDto findById(Long id) {
        return spotRepository.findById(id)
                .map(spotMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Spot not found with id: " + id));
    }

    @Override
    public Page<SpotDto> findAll(Pageable pageable) {
        return spotRepository.findAll(pageable)
                .map(spotMapper::toDto);

    }

    @Override
    public SpotDto updateSpot(Long id, SpotRegistrationDto spotRegistrationDto) {
        Spot spot = spotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("spot not found by id " + id));

        // Validación de propiedad (Ownership)
        User currentUser = getCurrentUser();

        if (!spot.getOwner().getId().equals(currentUser.getId())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "No tienes permiso para editar este alojamiento");
        }

        spot.setName(spotRegistrationDto.name());
        spot.setLocation(spotRegistrationDto.location());
        spot.setPricePerNight(spotRegistrationDto.pricePerNight());
        spot.setMaxCapacity(spotRegistrationDto.maxCapacity());
        spot.setTypeSpot(spotRegistrationDto.typeSpot());

        Spot spotSaved = spotRepository.save(spot);
        return spotMapper.toDto(spotSaved);
    }

    @Override
    public void deleteSpot(Long id) {
        Spot spot = spotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("spot not found with id: " + id));

        // Validación de propiedad (Ownership)
        User currentUser = getCurrentUser();

        if (!spot.getOwner().getId().equals(currentUser.getId())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "No tienes permiso para eliminar este alojamiento");
        }

        spotRepository.delete(spot);
    }

    @Override
    public Page<SpotDto> findMySpots(Pageable pageable) {
        User currentUser = getCurrentUser();
        return spotRepository.findByOwnerId(currentUser.getId(), pageable)
                .map(spotMapper::toDto);
    }

    private User getCurrentUser() {
        org.springframework.security.core.userdetails.UserDetails userDetails = (org.springframework.security.core.userdetails.UserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(
                        () -> new ResourceNotFoundException("User not found with email: " + userDetails.getUsername()));
    }

}
