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

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto save(UserRegistrationDto userRegistrationDto) {

        User user = UserMapper.toEntity(userRegistrationDto);
        User userSaved = userRepository.save(user);

        return UserMapper.toDto(userSaved);
    }

    public UserDto findById(UUID id) {
        return userRepository.findById(id)
                .map(user -> UserMapper.toDto(user))
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
                .map(userSaved -> UserMapper.toDto(userSaved))
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
                .map(UserMapper::toDto)
                .toList();
    }
}
