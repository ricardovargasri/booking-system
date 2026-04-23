
package com.booking_1.demo.user.services;

import com.booking_1.demo.user.dtos.UserDto;
import com.booking_1.demo.user.dtos.UserRegistrationDto;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUserService {
    UserDto save(UserRegistrationDto userRegistrationDto);

    UserDto findById(UUID id);

    Page<UserDto> findAll(Pageable pageable);

    UserDto updateUser(UUID id, UserRegistrationDto userRegistrationDto);

    void deleteUser(UUID id);
}
