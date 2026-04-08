package com.booking_1.demo.services.spotService;

import java.util.List;

import com.booking_1.demo.dtos.spotDtos.SpotDto;
import com.booking_1.demo.dtos.spotDtos.SpotRegistrationDto;

public interface ISpotService {
    SpotDto save(SpotRegistrationDto spotRegistrationDto);

    SpotDto findById(Long id);

    List<SpotDto> findAll();

    SpotDto updateSpot(Long id, SpotRegistrationDto spotRegistrationDto);

    void deleteSpot(Long id);

}
