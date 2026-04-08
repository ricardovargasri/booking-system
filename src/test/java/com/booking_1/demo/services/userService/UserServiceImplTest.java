package com.booking_1.demo.services.userService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.booking_1.demo.dtos.userDtos.UserDto;
import com.booking_1.demo.dtos.userDtos.UserRegistrationDto;
import com.booking_1.demo.entities.User;
import com.booking_1.demo.mappers.UserMapper;
import com.booking_1.demo.repositories.userRepository.UserRepository;
import com.booking_1.demo.services.userServices.UserServiceImpl;
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

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRegistrationDto registrationDto;
    private User userEntity;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        // Preparamos los datos de prueba
        registrationDto = new UserRegistrationDto("Ricardo", "ricardo@mail.com", "password123");

        userEntity = new User();
        userEntity.setName("Ricardo");
        userEntity.setEmail("ricardo@mail.com");

        userDto = new UserDto("Ricardo", "ricardo@mail.com", null);
    }

    @Test
    void save_ShouldReturnUserDto_WhenUserIsRegistered() {
        when(userMapper.toEntity(any(UserRegistrationDto.class))).thenReturn(userEntity);
        when(userRepository.save(any(User.class))).thenReturn(userEntity);
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        UserDto result = userService.save(registrationDto);

        assertNotNull(result);
        assertEquals("Ricardo", result.name());
        assertEquals("ricardo@mail.com", result.email());

        verify(userRepository).save(any(User.class));
    }
}
