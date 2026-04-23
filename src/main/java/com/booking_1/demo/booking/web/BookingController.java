package com.booking_1.demo.booking.web;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.booking_1.demo.booking.dtos.BookingDto;
import com.booking_1.demo.booking.dtos.BookingRegistrationDto;
import com.booking_1.demo.booking.services.IBookingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Tag(name = "Booking", description = "Endpoints for handling reservations and payments")
public class BookingController {

    private final IBookingService bookingService;

    @PostMapping
    @Operation(summary = "Create a new booking", description = "Processes a reservation request, calculates price, and sets initial status")
    public BookingDto save(@RequestBody BookingRegistrationDto bookingRegistrationDto) {
        return bookingService.save(bookingRegistrationDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking details", description = "Retrieves information about a specific reservation")
    public BookingDto findById(@PathVariable Long id) {
        return bookingService.findById(id);
    }

    @GetMapping
    @Operation(summary = "Get all bookings (Paginated)")
    public Page<BookingDto> findAll(Pageable pageable) {
        return bookingService.findAll(pageable);
    }

    @PatchMapping("/{id}/cancel")
    public BookingDto cancelBooking(@PathVariable Long id) {
        return bookingService.cancelBooking(id);
    }

}
