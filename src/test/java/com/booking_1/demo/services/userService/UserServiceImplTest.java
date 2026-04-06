package com.booking_1.demo.services.userService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.booking_1.demo.dtos.UserDto;
import com.booking_1.demo.dtos.UserRegistrationDto;
import com.booking_1.demo.entities.User;
import com.booking_1.demo.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRegistrationDto registrationDto;
    private User userEntity;

    @BeforeEach
    void setUp() {
        // Preparamos los datos de prueba
        registrationDto = new UserRegistrationDto("Ricardo", "ricardo@mail.com", "password123");
        
        userEntity = new User();
        userEntity.setName("Ricardo");
        userEntity.setEmail("ricardo@mail.com");
    }

    @Test
    void save_ShouldReturnUserDto_WhenUserIsRegistered() {
        // 1. Arrange: Simulamos que al guardar cualquier usuario, el repo devuelve nuestra entidad
        when(userRepository.save(any(User.class))).thenReturn(userEntity);

        // 2. Act: Ejecutamos el método que queremos probar
        UserDto result = userService.save(registrationDto);

        // 3. Assert: Verificamos que todo salió como esperábamos
        assertNotNull(result);
        assertEquals("Ricardo", result.name());
        assertEquals("ricardo@mail.com", result.email());

        // Verificamos que se llamó al repositorio exactamente una vez
        verify(userRepository).save(any(User.class));
    }
}
