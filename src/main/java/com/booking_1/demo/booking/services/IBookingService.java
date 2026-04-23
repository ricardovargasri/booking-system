package com.booking_1.demo.booking.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.booking_1.demo.booking.dtos.BookingDto;
import com.booking_1.demo.booking.dtos.BookingRegistrationDto;

public interface IBookingService {

    BookingDto cancelBooking(Long id);

    Page<BookingDto> findAll(Pageable pageable);

    BookingDto findById(Long id);

    BookingDto save(BookingRegistrationDto bookingRegistration);

}
