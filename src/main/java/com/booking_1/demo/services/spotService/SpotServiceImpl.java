package com.booking_1.demo.services.spotService;

import java.util.List;

import org.springframework.stereotype.Service;

import com.booking_1.demo.dtos.spotDtos.SpotDto;
import com.booking_1.demo.dtos.spotDtos.SpotRegistrationDto;
import com.booking_1.demo.entities.Spot;
import com.booking_1.demo.mappers.SpotMapper;
import com.booking_1.demo.repositories.spotRepository.SpotRepository;

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
                .orElseThrow(() -> new RuntimeException("spot not found"));
    }

    @Override
    public List<SpotDto> findAll() {
        return spotRepository.findAll().stream()
                .map(spotMapper::toDto)
                .toList();
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
                .orElseThrow(() -> new RuntimeException("spot not found by id " + id));
    }

    @Override
    public void deleteSpot(Long id) {
        Spot spot = spotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("user not found"));
        spotRepository.delete(spot);
    }

}
