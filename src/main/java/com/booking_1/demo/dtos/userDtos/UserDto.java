package com.booking_1.demo.dtos;

import java.time.LocalDateTime;

import com.booking_1.demo.enums.Rol;

public record UserDto(

        String name,
        String email,
        Rol rol

) {
}
