package com.bookin.demo.service;

import com.bookin.demo.dto.UserCreateDto;
import com.bookin.demo.dto.UserDto;
import com.bookin.demo.mapper.UserMapper;
import com.bookin.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserDto save(UserCreateDto userCreateDto) {
        var user = userMapper.toEntity(userCreateDto);
        return userMapper.toDto(userRepository.save(user));
    }

    public UserDto findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElse(null);
    }
}
