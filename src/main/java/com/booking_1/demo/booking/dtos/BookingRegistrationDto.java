package com.booking_1.demo.booking.dtos;

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
