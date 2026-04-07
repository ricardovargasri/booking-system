
package com.booking_1.demo.services.userServices;

import com.booking_1.demo.dtos.userDtos.UserDto;
import com.booking_1.demo.dtos.userDtos.UserRegistrationDto;
import java.util.List;
import java.util.UUID;

public interface IUserService {
    UserDto save(UserRegistrationDto userRegistrationDto);

    UserDto findById(UUID id);

    List<UserDto> findAll();

    UserDto updateUser(UUID id, UserRegistrationDto userRegistrationDto);

    void deleteUser(UUID id);
}
