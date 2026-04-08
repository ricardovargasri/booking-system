package com.booking_1.demo.dtos.bookingDtos;

import java.time.LocalDate;
import java.util.UUID;

public record BookingRegistrationDto(
        UUID guestId,
        Long spotId,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Integer numberOfGuests,
        String specialRequests) {
}
