package com.booking_1.demo.booking.web;

import java.util.List;

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
public class BookingController {

    private final IBookingService bookingService;

    @PostMapping
    public BookingDto save(@RequestBody BookingRegistrationDto bookingRegistrationDto) {
        return bookingService.save(bookingRegistrationDto);
    }

    @GetMapping("/{id}")
    public BookingDto findById(@PathVariable Long id) {
        return bookingService.findById(id);
    }

    @GetMapping
    public List<BookingDto> findAll() {
        return bookingService.findAll();
    }

    @PatchMapping("/{id}/cancel")
    public BookingDto cancelBooking(@PathVariable Long id) {
        return bookingService.cancelBooking(id);
    }

}
