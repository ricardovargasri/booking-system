package com.booking_1.demo.auth.dtos;

import com.booking_1.demo.user.dtos.UserDto;

public record AuthResponse(
                String token,
                UserDto user) {
}
