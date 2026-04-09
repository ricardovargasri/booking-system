package com.booking_1.demo.spot.dtos;

import java.util.UUID;

import com.booking_1.demo.core.enums.TypeSpot;

public record SpotRegistrationDto(
        String name,
        String location,
        Double pricePerNight,
        Integer maxCapacity,
        TypeSpot typeSpot,
        UUID ownerId) {

}
