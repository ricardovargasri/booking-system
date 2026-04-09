package com.booking_1.demo.booking.services;

import java.util.List;

import com.booking_1.demo.booking.dtos.BookingDto;
import com.booking_1.demo.booking.dtos.BookingRegistrationDto;

public interface IBookingService {

    BookingDto cancelBooking(Long id);

    List<BookingDto> findAll();

    BookingDto findById(Long id);

    BookingDto save(BookingRegistrationDto bookingRegistration);

}
