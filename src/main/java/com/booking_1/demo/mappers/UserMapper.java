package com.booking_1.demo.mappers;

import com.booking_1.demo.entities.User;
import com.booking_1.demo.enums.Rol;
import com.booking_1.demo.dtos.UserDto;
import com.booking_1.demo.dtos.UserRegistrationDto;

public class UserMapper {

    // Converts Entity to DTO (Matches UserDto with 6 fields)
    public static UserDto toDto(User user) {
        if (user == null) return null;
        return new UserDto(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRol(),
            user.getCreatedAt(),
            user.getUpdateAt()
        );
    }

    // Converts Registration DTO to Entity (Creates a new User)
    public static User toEntity(UserRegistrationDto registrationDto) {
        if (registrationDto == null) return null;
        User user = new User();
        user.setName(registrationDto.name());
        user.setEmail(registrationDto.email());
        user.setPassword(registrationDto.password()); // Plain text for now
        user.setRol(Rol.USER); // Default role is USER
        return user;
    }

    // Allows updating an existing User from a DTO
    public static User updateEntity(UserDto userDto) {
        if (userDto == null) return null;
        User user = new User();
        user.setId(userDto.id());
        user.setName(userDto.name());
        user.setEmail(userDto.email());
        user.setRol(userDto.rol());
        return user;
    }
}
