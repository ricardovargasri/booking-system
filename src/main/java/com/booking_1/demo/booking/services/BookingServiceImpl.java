package com.booking_1.demo.booking.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.booking_1.demo.booking.dtos.BookingDto;
import com.booking_1.demo.booking.dtos.BookingRegistrationDto;
import com.booking_1.demo.booking.entities.Booking;
import com.booking_1.demo.spot.entities.Spot;
import com.booking_1.demo.user.entities.User;
import com.booking_1.demo.core.enums.BookingStatus;
import com.booking_1.demo.core.enums.PaymentStatus;
import com.booking_1.demo.repositories.spotRepository.SpotRepository;
import com.booking_1.demo.booking.mappers.BookingMapper;
import com.booking_1.demo.booking.repositories.BookingRepository;
import com.booking_1.demo.user.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements IBookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final SpotRepository spotRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto save(BookingRegistrationDto bookingRegistration) {
        // 1. buscar las entidades
        User guest = userRepository.findById(bookingRegistration.guestId())
                .orElseThrow(() -> new RuntimeException(
                        "user not found by id " + bookingRegistration.guestId()));

        Spot spot = spotRepository.findById(bookingRegistration.spotId())
                .orElseThrow(() -> new RuntimeException("spot not found by id " + bookingRegistration.spotId()));

        // 2. validar reglas de negocio
        if (bookingRegistration.numberOfGuests() > spot.getMaxCapacity()) {
            throw new RuntimeException("es demasiada gente hermano");
        }
        if (!spot.getIsAvailable()) {
            throw new RuntimeException("alojamiento no disponible");
        }
        if (bookingRegistration.checkInDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("la fecha debe ser de este momento hacia adelante");
        }
        if (bookingRegistration.checkOutDate().isBefore(bookingRegistration.checkInDate())
                ||
                bookingRegistration.checkOutDate().isEqual(bookingRegistration.checkInDate())) {
            throw new RuntimeException("segun sus fechas se esta llendo antes de haber llegado");
        }
        // 3. matematicas
        long noches = ChronoUnit.DAYS.between(bookingRegistration.checkInDate(), bookingRegistration.checkOutDate());
        Double totalPrice = noches * spot.getPricePerNight();

        // 4. armar el recibo
        Booking booking = bookingMapper.toEntity(bookingRegistration);
        booking.setGuest(guest);
        booking.setSpot(spot);
        booking.setTotalPrice(totalPrice);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setStatus(BookingStatus.PENDING);

        // 5. guardar y devolver
        Booking bookingSaved = bookingRepository.save(booking);

        return bookingMapper.toDto(bookingSaved);
    }

    @Override
    public BookingDto findById(Long id) {
        return bookingRepository.findById(id)
                .map(bookingMapper::toDto)
                .orElseThrow(() -> new RuntimeException("reserva no encontrada"));
    }

    @Override
    public List<BookingDto> findAll() {
        return bookingRepository.findAll().stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public BookingDto cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("reserva no encontrada"));

        booking.setStatus(BookingStatus.CANCELLED);

        Booking bookingCanceled = bookingRepository.save(booking);

        return bookingMapper.toDto(bookingCanceled);
    }

}
