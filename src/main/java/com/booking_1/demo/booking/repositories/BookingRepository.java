package com.booking_1.demo.booking.repositories;

import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.booking_1.demo.booking.entities.Booking;
import com.booking_1.demo.core.enums.BookingStatus;

import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByGuestId(UUID guestId, Pageable pageable);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.spot.id = :spotId " +
            "AND b.status != :status " +
            "AND (b.checkInDate < :checkOut AND b.checkOutDate > :checkIn)")
    boolean existsOverlappingBooking(
            @Param("spotId") Long spotId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("status") BookingStatus status);
}
