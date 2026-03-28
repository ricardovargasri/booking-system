package com.booking_1.demo.services.userService;

import com.booking_1.demo.dtos.UserDto;
import com.booking_1.demo.dtos.UserRegistrationDto;
import com.booking_1.demo.entities.User;
import com.booking_1.demo.mappers.UserMapper;
import com.booking_1.demo.repositories.UserRepository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto save(UserRegistrationDto userRegistrationDto) {

        User user = UserMapper.toEntity(userRegistrationDto);
        User userSaved = userRepositoru.save(user);

        return UserMapper.toDto(userSaved);
    }

    public UserDto findById(UUID id) {
        return userRepository.findById(id)
                .map(user -> UserMapper.toDto(user))
                .orElseThrow(() -> new RuntimeException("User not found"));

    }

    public UserDto updateUser(UUID id, UserRegistrationDto userRegistrationDto) {

    }

    public void deleteUser(UUID id) {

    }
}
