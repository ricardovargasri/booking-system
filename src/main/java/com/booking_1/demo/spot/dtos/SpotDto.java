package com.booking_1.demo.spot.dtos;

public record SpotDto(
    Long id,
    String name,
    String location,
    Double pricePerNight,
    Integer maxCapacity,
    com.booking_1.demo.core.enums.TypeSpot typeSpot,
    Boolean isAvailable
) {}
