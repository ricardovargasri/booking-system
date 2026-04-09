package com.booking_1.demo.spot.services;

import java.util.List;

import com.booking_1.demo.spot.dtos.SpotDto;
import com.booking_1.demo.spot.dtos.SpotRegistrationDto;

public interface ISpotService {
    SpotDto save(SpotRegistrationDto spotRegistrationDto);

    SpotDto findById(Long id);

    List<SpotDto> findAll();

    SpotDto updateSpot(Long id, SpotRegistrationDto spotRegistrationDto);

    void deleteSpot(Long id);

}
