
package com.booking_1.demo.user.services;

import com.booking_1.demo.user.dtos.UserDto;
import com.booking_1.demo.user.dtos.UserRegistrationDto;
import java.util.List;
import java.util.UUID;

public interface IUserService {
    UserDto save(UserRegistrationDto userRegistrationDto);

    UserDto findById(UUID id);

    List<UserDto> findAll();

    UserDto updateUser(UUID id, UserRegistrationDto userRegistrationDto);

    void deleteUser(UUID id);
}
