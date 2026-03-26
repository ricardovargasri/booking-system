package com.bookin.demo.mapper;

import com.bookin.demo.dto.UserCreateDto;
import com.bookin.demo.dto.UserDto;
import com.bookin.demo.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    User toEntity(UserDto userDto);

    User toEntity(UserCreateDto userCreateDto);
}
