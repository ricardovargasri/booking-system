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
import com.booking_1.demo.spot.entities.Spot;
import com.booking_1.demo.spot.repositories.SpotRepository;
import com.booking_1.demo.user.entities.User;
import com.booking_1.demo.user.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SpotRepository spotRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void save_ShouldThrowBadRequestException_WhenGuestsExceedCapacity() {
        // Arrange (Organizar los datos)
        UUID guestId = UUID.randomUUID();
        Long spotId = 1L;
        
        // Creamos un DTO con 10 personas
        BookingRegistrationDto dto = new BookingRegistrationDto(
                guestId,
                spotId,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                10, 
                "Ninguna"
        );

        // Simulamos un usuario existente
        User mockUser = new User();
        mockUser.setId(guestId);

        // Simulamos un alojamiento que SOLO acepta 2 personas
        Spot mockSpot = new Spot();
        mockSpot.setId(spotId);
        mockSpot.setMaxCapacity(2); 
        mockSpot.setIsAvailable(true);

        // Configuramos los Mocks: "Cuando el servicio pregunte, responde esto"
        when(userRepository.findById(guestId)).thenReturn(Optional.of(mockUser));
        when(spotRepository.findByIdWithLock(spotId)).thenReturn(Optional.of(mockSpot));

        // Act & Assert (Actuar y Verificar)
        // Esperamos que al llamar a save(), se lance una BadRequestException
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            bookingService.save(dto);
        });

        // Verificamos que el mensaje de error sea el que pusiste en tu lógica
        assertEquals("es demasiada gente hermano", exception.getMessage());
    }

    @Test
    void save_ShouldThrowBadRequestException_WhenStayExceeds30Days() {
        // Arrange
        UUID guestId = UUID.randomUUID();
        Long spotId = 1L;
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = checkIn.plusDays(31); // 31 días de estancia

        BookingRegistrationDto dto = new BookingRegistrationDto(
                guestId, spotId, checkIn, checkOut, 1, "Vacaciones largas"
        );

        User mockUser = new User();
        mockUser.setId(guestId);

        Spot mockSpot = new Spot();
        mockSpot.setId(spotId);
        mockSpot.setMaxCapacity(5); // Capacidad de sobra
        mockSpot.setPricePerNight(100.0);
        mockSpot.setIsAvailable(true);

        when(userRepository.findById(guestId)).thenReturn(Optional.of(mockUser));
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
        LocalDate checkIn = LocalDate.now().minusDays(1); // Ayer
        LocalDate checkOut = LocalDate.now().plusDays(2);

        BookingRegistrationDto dto = new BookingRegistrationDto(
                guestId, spotId, checkIn, checkOut, 1, "Reserva retroactiva"
        );

        User mockUser = new User();
        mockUser.setId(guestId);

        Spot mockSpot = new Spot();
        mockSpot.setId(spotId);
        mockSpot.setMaxCapacity(5);
        mockSpot.setIsAvailable(true);

        when(userRepository.findById(guestId)).thenReturn(Optional.of(mockUser));
        when(spotRepository.findByIdWithLock(spotId)).thenReturn(Optional.of(mockSpot));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            bookingService.save(dto);
        });

        assertEquals("la fecha debe ser de este momento hacia adelante", exception.getMessage());
    }

    @Test
    void save_ShouldThrowBadRequestException_WhenCheckOutIsBeforeCheckIn() {
        // Arrange
        UUID guestId = UUID.randomUUID();
        Long spotId = 1L;
        LocalDate checkIn = LocalDate.now().plusDays(5);
        LocalDate checkOut = LocalDate.now().plusDays(3); // Checkout antes que Checkin

        BookingRegistrationDto dto = new BookingRegistrationDto(
                guestId, spotId, checkIn, checkOut, 1, "Fechas invertidas"
        );

        User mockUser = new User();
        mockUser.setId(guestId);

        Spot mockSpot = new Spot();
        mockSpot.setId(spotId);
        mockSpot.setMaxCapacity(5);
        mockSpot.setIsAvailable(true);

        when(userRepository.findById(guestId)).thenReturn(Optional.of(mockUser));
        when(spotRepository.findByIdWithLock(spotId)).thenReturn(Optional.of(mockSpot));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            bookingService.save(dto);
        });

        assertEquals("segun sus fechas se esta llendo antes de haber llegado", exception.getMessage());
    }

    @Test
    void save_ShouldThrowBadRequestException_WhenSpotIsNotAvailable() {
        // Arrange
        UUID guestId = UUID.randomUUID();
        Long spotId = 1L;
        
        BookingRegistrationDto dto = new BookingRegistrationDto(
                guestId, spotId, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3), 1, "Cualquier cosa"
        );

        User mockUser = new User();
        mockUser.setId(guestId);

        // Simulamos un alojamiento que NO está disponible
        Spot mockSpot = new Spot();
        mockSpot.setId(spotId);
        mockSpot.setIsAvailable(false); 
        mockSpot.setMaxCapacity(10); // Agregado para evitar NullPointerException en el check previo

        when(userRepository.findById(guestId)).thenReturn(Optional.of(mockUser));
        when(spotRepository.findByIdWithLock(spotId)).thenReturn(Optional.of(mockSpot));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            bookingService.save(dto);
        });

        assertEquals("Spot is not available", exception.getMessage());
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

        BookingRegistrationDto dto = new BookingRegistrationDto(
                guestId, spotId, checkIn, checkOut, 2, "Trip"
        );

        User guest = new User();
        guest.setId(guestId);

        Spot spot = new Spot();
        spot.setId(spotId);
        spot.setMaxCapacity(5);
        spot.setPricePerNight(pricePerNight);
        spot.setIsAvailable(true);

        com.booking_1.demo.booking.entities.Booking mockBookingEntity = new com.booking_1.demo.booking.entities.Booking();
        
        when(userRepository.findById(guestId)).thenReturn(Optional.of(guest));
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

        BookingRegistrationDto dto = new BookingRegistrationDto(
                guestId, spotId, checkIn, checkOut, 2, "Trip"
        );

        User guest = new User();
        guest.setId(guestId);

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

        when(userRepository.findById(guestId)).thenReturn(Optional.of(guest));
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
