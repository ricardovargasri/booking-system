package com.booking_1.demo.booking.dtos;

import java.time.LocalDate;

public record BookingRegistrationDto(
        Long spotId,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Integer numberOfGuests,
        String specialRequests) {
}
