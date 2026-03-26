package com.booking_1.demo.dtos;

import java.time.LocalDateTime;
import java.util.UUID;
import com.booking_1.demo.enums.Rol;

public record UserDto(
    UUID id,
    String name,
    String email,
    Rol rol,
    LocalDateTime createdAt,
    LocalDateTime updateAt
) {
}
