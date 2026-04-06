package com.booking_1.demo.dtos.spotDtos;

import java.util.UUID;

import com.booking_1.demo.enums.TypeSpot;

public record SpotRegistrationDto(
        String name,
        String location,
        Double pricePerNight,
        Integer maxCapacity,
        TypeSpot typeSpot,
        UUID ownerId) {

}
