package com.bookin.demo.dto;

public record UserCreateDto(
        String name,
        String email,
        String password) {
}
