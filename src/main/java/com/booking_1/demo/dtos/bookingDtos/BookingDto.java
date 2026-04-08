package com.booking_1.demo.dtos.bookingDtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.booking_1.demo.enums.BookingStatus;
import com.booking_1.demo.enums.PaymentStatus;

public record BookingDto(
        Long id,
        UUID guestId,
        Long spotId,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        LocalDateTime createdAt,
        BookingStatus status,
        Double totalPrice,
        PaymentStatus paymentStatus,
        Integer numberOfGuests,
        String specialRequests) {
}
