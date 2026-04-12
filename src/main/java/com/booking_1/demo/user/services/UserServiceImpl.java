package com.booking_1.demo.user.services;

import com.booking_1.demo.core.enums.Rol;
import com.booking_1.demo.user.dtos.UserDto;
import com.booking_1.demo.user.dtos.UserRegistrationDto;
import com.booking_1.demo.user.entities.User;
import com.booking_1.demo.user.mappers.UserMapper;
import com.booking_1.demo.user.repositories.UserRepository;

import java.util.List;

import java.util.UUID;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto save(UserRegistrationDto userRegistrationDto) {

        User user = userMapper.toEntity(userRegistrationDto);
        user.setRol(Rol.USER);
        User userSaved = userRepository.save(user);

        return userMapper.toDto(userSaved);
    }

    public UserDto findById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new RuntimeException("User not found"));

    }

    public UserDto updateUser(UUID id, UserRegistrationDto userRegistrationDto) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(userRegistrationDto.name());
                    user.setEmail(userRegistrationDto.email());
                    user.setPassword(userRegistrationDto.password());

                    return userRepository.save(user);
                })
                .map(userMapper::toDto)
                .orElseThrow(() -> new RuntimeException("user not found by id " + id));

    }

    public void deleteUser(UUID id) {
        userRepository.findById(id)
                .map(u -> {
                    userRepository.delete(u);
                    return u;
                })
                .orElseThrow(() -> new RuntimeException("user not found by id " + id));
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }
}
