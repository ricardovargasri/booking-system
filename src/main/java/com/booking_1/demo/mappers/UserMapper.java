package com.booking_1.demo.mappers;

import com.booking_1.demo.dtos.userDtos.UserDto;
import com.booking_1.demo.dtos.userDtos.UserRegistrationDto;
import com.booking_1.demo.entities.User;
import com.booking_1.demo.enums.Rol;

import java.util.Optional;

public class UserMapper {

    // Converts Entity to DTO (Matches UserDto with 6 fields)
    public static UserDto toDto(User user) {
        return Optional.ofNullable(user)
                .map(u -> new UserDto(
                        u.getName(),
                        u.getEmail(),
                        u.getRol()))
                .orElse(null);
    }

    // Converts Registration DTO to Entity (Creates a new User)
    public static User toEntity(UserRegistrationDto registrationDto) {
        return Optional.ofNullable(registrationDto)
                .map(dto -> {
                    User user = new User();
                    user.setName(dto.name());
                    user.setEmail(dto.email());
                    user.setPassword(dto.password());
                    user.setRol(Rol.USER);
                    return user;
                })
                .orElse(null);

    }
}
