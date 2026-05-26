package com.booking_1.demo.spot.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.booking_1.demo.core.exceptions.ResourceNotFoundException;
import com.booking_1.demo.core.security.services.SecurityService;
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
    private final SecurityService securityService;

    @Override
    public SpotCreationResponse save(SpotRegistrationDto spotRegistrationDto) {
        User owner = securityService.getCurrentUser();
        
        Spot spot = spotMapper.spotToEntity(spotRegistrationDto);
        spot.setOwner(owner);
        spot.setIsAvailable(true);

        Spot spotSaved = spotRepository.save(spot);
        SpotDto spotDto = spotMapper.toDto(spotSaved);

        return new SpotCreationResponse(spotDto, null);
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

        User currentUser = securityService.getCurrentUser();

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

        User currentUser = securityService.getCurrentUser();

        if (!spot.getOwner().getId().equals(currentUser.getId())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "No tienes permiso para eliminar este alojamiento");
        }

        spotRepository.delete(spot);
    }

    @Override
    public Page<SpotDto> findMySpots(Pageable pageable) {
        User currentUser = securityService.getCurrentUser();
        return spotRepository.findByOwnerId(currentUser.getId(), pageable)
                .map(spotMapper::toDto);
    }
}
