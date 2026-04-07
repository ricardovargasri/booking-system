package com.booking_1.demo.services.userServices;

import com.booking_1.demo.dtos.userDtos.UserDto;
import com.booking_1.demo.dtos.userDtos.UserRegistrationDto;
import com.booking_1.demo.entities.User;
import com.booking_1.demo.mappers.UserMapper;
import com.booking_1.demo.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDto save(UserRegistrationDto userRegistrationDto) {

        User user = userMapper.toEntity(userRegistrationDto);
        User userSaved = userRepository.save(user);

        return userMapper.toDto(userSaved);
    }

    public UserDto findById(UUID id) {
        return userRepository.findById(id)
                .map(user -> userMapper.toDto(user))
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
                .map(userSaved -> userMapper.toDto(userSaved))
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
