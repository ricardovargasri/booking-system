package com.booking_1.demo.booking.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.booking_1.demo.booking.dtos.BookingRegistrationDto;
import com.booking_1.demo.booking.mappers.BookingMapper;
import com.booking_1.demo.booking.repositories.BookingRepository;
import com.booking_1.demo.core.exceptions.BadRequestException;
import com.booking_1.demo.core.security.services.SecurityService;
import com.booking_1.demo.spot.entities.Spot;
import com.booking_1.demo.spot.repositories.SpotRepository;
import com.booking_1.demo.user.entities.User;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private SpotRepository spotRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    // --- Helper para crear un usuario autenticado mock ---
    private User mockAuthenticatedUser(UUID guestId) {
        User mockUser = new User();
        mockUser.setId(guestId);
        when(securityService.getCurrentUser()).thenReturn(mockUser);
        return mockUser;
    }

    @Test
    void save_ShouldThrowBadRequestException_WhenGuestsExceedCapacity() {
        // Arrange
        UUID guestId = UUID.randomUUID();
        Long spotId = 1L;

        mockAuthenticatedUser(guestId);

        BookingRegistrationDto dto = new BookingRegistrationDto(
                spotId,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                10,
                "Ninguna"
        );

        Spot mockSpot = new Spot();
        mockSpot.setId(spotId);
        mockSpot.setMaxCapacity(2);
        mockSpot.setIsAvailable(true);

        when(spotRepository.findByIdWithLock(spotId)).thenReturn(Optional.of(mockSpot));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            bookingService.save(dto);
        });

        assertEquals("La cantidad de huéspedes excede la capacidad", exception.getMessage());
    }

    @Test
    void save_ShouldThrowBadRequestException_WhenStayExceeds30Days() {
        // Arrange
        UUID guestId = UUID.randomUUID();
        Long spotId = 1L;
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = checkIn.plusDays(31);

        mockAuthenticatedUser(guestId);

        BookingRegistrationDto dto = new BookingRegistrationDto(
                spotId, checkIn, checkOut, 1, "Vacaciones largas"
        );

        Spot mockSpot = new Spot();
        mockSpot.setId(spotId);
        mockSpot.setMaxCapacity(5);
        mockSpot.setPricePerNight(100.0);
        mockSpot.setIsAvailable(true);

        when(spotRepository.findByIdWithLock(spotId)).thenReturn(Optional.of(mockSpot));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            bookingService.save(dto);
        });

        assertEquals("No puedes reservar por más de 30 días", exception.getMessage());
    }

    @Test
    void save_ShouldThrowBadRequestException_WhenCheckInIsInThePast() {
        // Arrange
        UUID guestId = UUID.randomUUID();
        Long spotId = 1L;
        LocalDate checkIn = LocalDate.now().minusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(2);

        mockAuthenticatedUser(guestId);

        BookingRegistrationDto dto = new BookingRegistrationDto(
                spotId, checkIn, checkOut, 1, "Reserva retroactiva"
        );

        Spot mockSpot = new Spot();
        mockSpot.setId(spotId);
        mockSpot.setMaxCapacity(5);
        mockSpot.setIsAvailable(true);

        when(spotRepository.findByIdWithLock(spotId)).thenReturn(Optional.of(mockSpot));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            bookingService.save(dto);
        });

        assertEquals("La fecha de inicio no puede ser en el pasado", exception.getMessage());
    }

    @Test
    void save_ShouldThrowBadRequestException_WhenCheckOutIsBeforeCheckIn() {
        // Arrange
        UUID guestId = UUID.randomUUID();
        Long spotId = 1L;
        LocalDate checkIn = LocalDate.now().plusDays(5);
        LocalDate checkOut = LocalDate.now().plusDays(3);

        mockAuthenticatedUser(guestId);

        BookingRegistrationDto dto = new BookingRegistrationDto(
                spotId, checkIn, checkOut, 1, "Fechas invertidas"
        );

        Spot mockSpot = new Spot();
        mockSpot.setId(spotId);
        mockSpot.setMaxCapacity(5);
        mockSpot.setIsAvailable(true);

        when(spotRepository.findByIdWithLock(spotId)).thenReturn(Optional.of(mockSpot));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            bookingService.save(dto);
        });

        assertEquals("La fecha de salida debe ser posterior a la de entrada", exception.getMessage());
    }

    @Test
    void save_ShouldThrowBadRequestException_WhenSpotIsNotAvailable() {
        // Arrange
        UUID guestId = UUID.randomUUID();
        Long spotId = 1L;

        mockAuthenticatedUser(guestId);

        BookingRegistrationDto dto = new BookingRegistrationDto(
                spotId, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3), 1, "Cualquier cosa"
        );

        Spot mockSpot = new Spot();
        mockSpot.setId(spotId);
        mockSpot.setIsAvailable(false);
        mockSpot.setMaxCapacity(10);

        when(spotRepository.findByIdWithLock(spotId)).thenReturn(Optional.of(mockSpot));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            bookingService.save(dto);
        });

        assertEquals("El alojamiento no está disponible", exception.getMessage());
    }

    @Test
    void save_ShouldCalculateTotalPriceCorrectly() {
        // Arrange
        UUID guestId = UUID.randomUUID();
        Long spotId = 1L;
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(4); // 3 noches
        double pricePerNight = 100.0;
        double expectedTotal = 300.0;

        mockAuthenticatedUser(guestId);

        BookingRegistrationDto dto = new BookingRegistrationDto(
                spotId, checkIn, checkOut, 2, "Trip"
        );

        Spot spot = new Spot();
        spot.setId(spotId);
        spot.setMaxCapacity(5);
        spot.setPricePerNight(pricePerNight);
        spot.setIsAvailable(true);

        com.booking_1.demo.booking.entities.Booking mockBookingEntity = new com.booking_1.demo.booking.entities.Booking();

        when(spotRepository.findByIdWithLock(spotId)).thenReturn(Optional.of(spot));
        when(bookingRepository.existsOverlappingBooking(any(), any(), any(), any())).thenReturn(false);
        when(bookingMapper.toEntity(dto)).thenReturn(mockBookingEntity);
        when(bookingRepository.save(any())).thenReturn(mockBookingEntity);

        // Act
        bookingService.save(dto);

        // Assert
        assertEquals(expectedTotal, mockBookingEntity.getTotalPrice());
    }

    @Test
    void save_ShouldReturnBookingDto_WhenAllConditionsAreMet() {
        // Arrange
        UUID guestId = UUID.randomUUID();
        Long spotId = 1L;
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(3);

        mockAuthenticatedUser(guestId);

        BookingRegistrationDto dto = new BookingRegistrationDto(
                spotId, checkIn, checkOut, 2, "Trip"
        );

        Spot spot = new Spot();
        spot.setId(spotId);
        spot.setMaxCapacity(5);
        spot.setPricePerNight(100.0);
        spot.setIsAvailable(true);

        com.booking_1.demo.booking.entities.Booking mockBookingEntity = new com.booking_1.demo.booking.entities.Booking();
        com.booking_1.demo.booking.dtos.BookingDto expectedDto = new com.booking_1.demo.booking.dtos.BookingDto(
                1L,
                guestId,
                spotId,
                checkIn,
                checkOut,
                java.time.LocalDateTime.now(),
                com.booking_1.demo.core.enums.BookingStatus.PENDING,
                200.0,
                com.booking_1.demo.core.enums.PaymentStatus.PENDING_PAYMENT,
                2,
                "Trip"
        );

        when(spotRepository.findByIdWithLock(spotId)).thenReturn(Optional.of(spot));
        when(bookingRepository.existsOverlappingBooking(any(), any(), any(), any())).thenReturn(false);
        when(bookingMapper.toEntity(dto)).thenReturn(mockBookingEntity);
        when(bookingRepository.save(any())).thenReturn(mockBookingEntity);
        when(bookingMapper.toDto(mockBookingEntity)).thenReturn(expectedDto);

        // Act
        com.booking_1.demo.booking.dtos.BookingDto result = bookingService.save(dto);

        // Assert
        assertEquals(expectedDto.id(), result.id());
        assertEquals(expectedDto.totalPrice(), result.totalPrice());
        assertEquals(com.booking_1.demo.core.enums.BookingStatus.PENDING, result.status());
    }
}
