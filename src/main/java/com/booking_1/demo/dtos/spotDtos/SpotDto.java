package com.booking_1.demo.dtos.spotDtos;

public record SpotDto(
    String name,
    String location,
    Double pricePerNight,
    Integer maxCapacity,
    com.booking_1.demo.enums.TypeSpot typeSpot
) {}
