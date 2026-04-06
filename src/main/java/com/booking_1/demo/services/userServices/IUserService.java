
package com.booking_1.demo.services.userService;

import com.booking_1.demo.dtos.UserDto;
import com.booking_1.demo.dtos.UserRegistrationDto;
import java.util.List;
import java.util.UUID;

public interface IUserService {
    UserDto save(UserRegistrationDto userRegistrationDto);
    UserDto findById(UUID id);
    List<UserDto> findAll();
    UserDto updateUser(UUID id, UserRegistrationDto userRegistrationDto);
    void deleteUser(UUID id);
}
