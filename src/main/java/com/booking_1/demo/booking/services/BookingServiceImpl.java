package com.booking_1.demo.booking.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.booking_1.demo.booking.dtos.BookingDto;
import com.booking_1.demo.booking.dtos.BookingRegistrationDto;
import com.booking_1.demo.booking.entities.Booking;
import com.booking_1.demo.spot.entities.Spot;
import com.booking_1.demo.user.entities.User;
import com.booking_1.demo.core.enums.BookingStatus;
import com.booking_1.demo.core.enums.PaymentStatus;
import com.booking_1.demo.core.exceptions.BadRequestException;
import com.booking_1.demo.core.exceptions.ResourceNotFoundException;
import com.booking_1.demo.core.security.services.SecurityService;
import com.booking_1.demo.spot.repositories.SpotRepository;
import com.booking_1.demo.booking.mappers.BookingMapper;
import com.booking_1.demo.booking.repositories.BookingRepository;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements IBookingService {

    private final BookingRepository bookingRepository;
    private final SpotRepository spotRepository;
    private final BookingMapper bookingMapper;
    private final SecurityService securityService;

    @Transactional
    @Override
    public BookingDto save(BookingRegistrationDto bookingRegistration) {
        // 1. Obtener usuario autenticado del token de Keycloak (con autocreación JIT si es necesario)
        User guest = securityService.getCurrentUser();

        Spot spot = spotRepository.findByIdWithLock(bookingRegistration.spotId())
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado"));

        // 2. validar reglas de negocio
        if (bookingRegistration.numberOfGuests() > spot.getMaxCapacity()) {
            throw new BadRequestException("La cantidad de huéspedes excede la capacidad");
        }
        if (!spot.getIsAvailable()) {
            throw new BadRequestException("El alojamiento no está disponible");
        }
        if (bookingRegistration.checkInDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("La fecha de inicio no puede ser en el pasado");
        }
        if (!bookingRegistration.checkOutDate().isAfter(bookingRegistration.checkInDate())) {
            throw new BadRequestException("La fecha de salida debe ser posterior a la de entrada");
        }

        long noches = ChronoUnit.DAYS.between(bookingRegistration.checkInDate(), bookingRegistration.checkOutDate());
        if (noches > 30) {
            throw new BadRequestException("No puedes reservar por más de 30 días");
        }

        boolean isOverlapping = bookingRepository.existsOverlappingBooking(
                spot.getId(),
                bookingRegistration.checkInDate(),
                bookingRegistration.checkOutDate(),
                BookingStatus.CANCELLED);
        if (isOverlapping) {
            throw new BadRequestException("El alojamiento ya está reservado en esas fechas");
        }

        // 3. Cálculos
        Double totalPrice = noches * spot.getPricePerNight();

        // 4. Armar entidad
        Booking booking = bookingMapper.toEntity(bookingRegistration);
        booking.setGuest(guest);
        booking.setSpot(spot);
        booking.setTotalPrice(totalPrice);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setStatus(BookingStatus.PENDING);
        booking.setPaymentStatus(PaymentStatus.PENDING_PAYMENT);

        // 5. Guardar
        Booking bookingSaved = bookingRepository.save(booking);
        return bookingMapper.toDto(bookingSaved);
    }

    @Override
    public BookingDto findById(Long id) {
        return bookingRepository.findById(id)
                .map(bookingMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));
    }

    @Override
    @Operation
    public Page<BookingDto> findAll(Pageable pageable) {
        return bookingRepository.findAll(pageable)
                .map(bookingMapper::toDto);
    }

    @Override
    public BookingDto cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        // Opcional: Validar que el usuario que cancela es el dueño de la reserva
        User currentUser = securityService.getCurrentUser();
        if (!booking.getGuest().getId().equals(currentUser.getId())) {
             throw new org.springframework.security.access.AccessDeniedException("No puedes cancelar una reserva que no es tuya");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking bookingCanceled = bookingRepository.save(booking);
        return bookingMapper.toDto(bookingCanceled);
    }

    @Override
    public Page<BookingDto> FindMyBookings(Pageable pageable) {
        User currentUser = securityService.getCurrentUser();
        return bookingRepository.findByGuestId(currentUser.getId(), pageable)
                .map(bookingMapper::toDto);
    }
}
