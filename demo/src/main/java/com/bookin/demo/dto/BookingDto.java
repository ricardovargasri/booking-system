package com.bookin.demo.dto;

import java.time.LocalDateTime;

public record BookingDto(
        Long id,
        UserDto user,
        RoomDto room,
        LocalDateTime startTime,
        LocalDateTime endTime) {
}
