package com.booking_1.demo.spot.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.booking_1.demo.spot.dtos.SpotDto;
import com.booking_1.demo.spot.dtos.SpotRegistrationDto;

public interface ISpotService {
    SpotDto save(SpotRegistrationDto spotRegistrationDto);

    SpotDto findById(Long id);

    Page<SpotDto> findAll(Pageable pageable);

    SpotDto updateSpot(Long id, SpotRegistrationDto spotRegistrationDto);

    void deleteSpot(Long id);

}
