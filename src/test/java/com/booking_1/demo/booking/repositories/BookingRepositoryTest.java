package com.booking_1.demo.booking.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.booking_1.demo.booking.entities.Booking;
import com.booking_1.demo.core.enums.BookingStatus;
import com.booking_1.demo.core.enums.TypeSpot;
import com.booking_1.demo.spot.entities.Spot;
import com.booking_1.demo.user.entities.User;

@DataJpaTest // Levanta H2 y configura JPA automáticamente
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TestEntityManager entityManager; // Herramienta para persistir datos de prueba

    private Spot savedSpot;
    private User savedUser;

    @BeforeEach
    void setUp() {
        // 1. Creamos un usuario (Dueño/Huésped)
        User user = new User();
        user.setName("Ricardo");
        user.setEmail("ricardo@example.com");
        user.setPassword("password");
        user.setCreatedAt(java.time.LocalDateTime.now());
        savedUser = entityManager.persistFlushFind(user);

        // 2. Creamos un Spot
        Spot spot = new Spot();
        spot.setName("Cabaña del Bosque");
        spot.setLocation("Montaña");
        spot.setPricePerNight(150.0);
        spot.setMaxCapacity(4);
        spot.setTypeSpot(TypeSpot.CABANA);
        spot.setIsAvailable(true);
        spot.setOwner(savedUser);
        spot.setCreatedAt(java.time.LocalDateTime.now());
        savedSpot = entityManager.persistFlushFind(spot);

        // 3. Creamos una reserva base (del 10 al 15 del mes)
        Booking booking = new Booking();
        booking.setGuest(savedUser);
        booking.setSpot(savedSpot);
        booking.setCheckInDate(LocalDate.now().plusDays(10));
        booking.setCheckOutDate(LocalDate.now().plusDays(15));
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setNumberOfGuests(2);
        booking.setTotalPrice(750.0);
        booking.setCreatedAt(java.time.LocalDateTime.now());
        booking.setPaymentStatus(com.booking_1.demo.core.enums.PaymentStatus.PENDING_PAYMENT);
        entityManager.persistFlushFind(booking);
    }

    @Test
    @DisplayName("Debe detectar conflicto cuando las fechas coinciden exactamente")
    void shouldDetectOverlapExactDates() {
        LocalDate checkIn = LocalDate.now().plusDays(10);
        LocalDate checkOut = LocalDate.now().plusDays(15);

        boolean exists = bookingRepository.existsOverlappingBooking(
            savedSpot.getId(), checkIn, checkOut, BookingStatus.CANCELLED);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Debe detectar conflicto cuando la nueva reserva empieza durante una existente")
    void shouldDetectOverlapPartialStart() {
        LocalDate checkIn = LocalDate.now().plusDays(12); // Solapa con la reserva del 10 al 15
        LocalDate checkOut = LocalDate.now().plusDays(18);

        boolean exists = bookingRepository.existsOverlappingBooking(
            savedSpot.getId(), checkIn, checkOut, BookingStatus.CANCELLED);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Debe permitir reserva cuando las fechas no se cruzan")
    void shouldNotDetectOverlapWhenDatesAreFree() {
        LocalDate checkIn = LocalDate.now().plusDays(20);
        LocalDate checkOut = LocalDate.now().plusDays(25);

        boolean exists = bookingRepository.existsOverlappingBooking(
            savedSpot.getId(), checkIn, checkOut, BookingStatus.CANCELLED);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("No debe detectar conflicto si la reserva existente está CANCELADA")
    void shouldIgnoreCancelledBookings() {
        // Creamos una reserva CANCELADA en fechas que chocarían
        Booking cancelled = new Booking();
        cancelled.setGuest(savedUser);
        cancelled.setSpot(savedSpot);
        cancelled.setCheckInDate(LocalDate.now().plusDays(30));
        cancelled.setCheckOutDate(LocalDate.now().plusDays(35));
        cancelled.setStatus(BookingStatus.CANCELLED);
        cancelled.setCreatedAt(java.time.LocalDateTime.now());
        cancelled.setPaymentStatus(com.booking_1.demo.core.enums.PaymentStatus.PENDING_PAYMENT);
        cancelled.setNumberOfGuests(1);
        cancelled.setTotalPrice(100.0);
        entityManager.persist(cancelled);

        // Intentamos reservar los mismos días (30 al 35)
        boolean exists = bookingRepository.existsOverlappingBooking(
            savedSpot.getId(), 
            LocalDate.now().plusDays(30), 
            LocalDate.now().plusDays(35), 
            BookingStatus.CANCELLED);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Debe detectar conflicto cuando la nueva reserva envuelve a una existente")
    void shouldDetectOverlapEnveloping() {
        LocalDate checkIn = LocalDate.now().plusDays(5); // Empieza antes (existente es el 10)
        LocalDate checkOut = LocalDate.now().plusDays(20); // Termina después (existente es el 15)

        boolean exists = bookingRepository.existsOverlappingBooking(
            savedSpot.getId(), checkIn, checkOut, BookingStatus.CANCELLED);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Debe detectar conflicto cuando la nueva reserva está contenida en una existente")
    void shouldDetectOverlapInternal() {
        LocalDate checkIn = LocalDate.now().plusDays(11); // Dentro del 10 al 15
        LocalDate checkOut = LocalDate.now().plusDays(14); // Dentro del 10 al 15

        boolean exists = bookingRepository.existsOverlappingBooking(
            savedSpot.getId(), checkIn, checkOut, BookingStatus.CANCELLED);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Debe detectar conflicto cuando la nueva reserva termina durante una existente")
    void shouldDetectOverlapPartialEnd() {
        LocalDate checkIn = LocalDate.now().plusDays(5);
        LocalDate checkOut = LocalDate.now().plusDays(12); // Solapa con el inicio de la existente (10)

        boolean exists = bookingRepository.existsOverlappingBooking(
            savedSpot.getId(), checkIn, checkOut, BookingStatus.CANCELLED);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Debe persistir la reserva y mantener las relaciones con Usuario y Spot")
    void shouldPersistBookingWithCorrectRelationships() {
        // Arrange
        Booking newBooking = new Booking();
        newBooking.setGuest(savedUser);
        newBooking.setSpot(savedSpot);
        newBooking.setCheckInDate(LocalDate.now().plusDays(40));
        newBooking.setCheckOutDate(LocalDate.now().plusDays(45));
        newBooking.setStatus(BookingStatus.PENDING);
        newBooking.setNumberOfGuests(1);
        newBooking.setTotalPrice(100.0);
        newBooking.setCreatedAt(java.time.LocalDateTime.now());
        newBooking.setPaymentStatus(com.booking_1.demo.core.enums.PaymentStatus.PENDING_PAYMENT);

        // Act
        Booking saved = bookingRepository.save(newBooking);
        entityManager.flush();
        entityManager.clear(); // Limpiamos el cache para forzar lectura de BD

        // Assert
        Booking retrieved = bookingRepository.findById(saved.getId()).orElseThrow();
        
        assertThat(retrieved.getGuest()).isNotNull();
        assertThat(retrieved.getGuest().getId()).isEqualTo(savedUser.getId());
        assertThat(retrieved.getSpot()).isNotNull();
        assertThat(retrieved.getSpot().getId()).isEqualTo(savedSpot.getId());
        assertThat(retrieved.getStatus()).isEqualTo(BookingStatus.PENDING);
    }
}
