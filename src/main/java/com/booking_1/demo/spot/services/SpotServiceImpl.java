package com.booking_1.demo.spot.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.booking_1.demo.core.exceptions.ResourceNotFoundException;
import com.booking_1.demo.spot.repositories.SpotRepository;
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

    @Override
    public SpotDto save(SpotRegistrationDto spotRegistrationDto) {
        Spot spot = spotMapper.spotToEntity(spotRegistrationDto);
        Spot spotSaved = spotRepository.save(spot);
        return spotMapper.toDto(spotSaved);

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
        return spotRepository.findById(id)
                .map(s -> {
                    s.setName(spotRegistrationDto.name());
                    s.setLocation(spotRegistrationDto.location());
                    s.setPricePerNight(spotRegistrationDto.pricePerNight());
                    s.setMaxCapacity(spotRegistrationDto.maxCapacity());
                    s.setTypeSpot(spotRegistrationDto.typeSpot());

                    return spotRepository.save(s);
                })
                .map(spotMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("spot not found by id " + id));
    }

    @Override
    public void deleteSpot(Long id) {
        Spot spot = spotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("spot not found with id: " + id));
        spotRepository.delete(spot);
    }

}
