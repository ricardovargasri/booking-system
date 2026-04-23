package com.booking_1.demo.user.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.booking_1.demo.auth.dtos.AuthResponse;
import com.booking_1.demo.user.dtos.UserDto;
import com.booking_1.demo.user.dtos.UserRegistrationDto;
import com.booking_1.demo.user.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rol", constant = "USER")
    User toEntity(UserRegistrationDto registrationDto);

    @Mapping(target = "token", source = "token")
    @Mapping(target = "user", source = "user")
    AuthResponse toAuthResponse(User user, String token);
}
