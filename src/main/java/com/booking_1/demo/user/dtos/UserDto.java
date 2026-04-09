package com.booking_1.demo.user.dtos;

import com.booking_1.demo.core.enums.Rol;

public record UserDto(

        String name,
        String email,
        Rol rol

) {
}
