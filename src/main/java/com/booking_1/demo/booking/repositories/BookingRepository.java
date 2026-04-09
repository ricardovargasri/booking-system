package com.booking_1.demo.booking.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.booking_1.demo.booking.entities.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {

}
