package com.booking_1.demo.booking.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
}
