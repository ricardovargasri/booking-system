package com.bookin.demo.controller;

import com.bookin.demo.dto.BookingDto;
import com.bookin.demo.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public List<BookingDto> getAllBookings() {
        return bookingService.findAll();
    }

    @PostMapping("/user/{userId}/room/{roomId}")
    public BookingDto createBooking(@PathVariable Long userId, @PathVariable Long roomId,
            @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(userId, roomId, bookingDto);
    }
}
