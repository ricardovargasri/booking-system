package com.booking_1.demo.controllers;

import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.booking_1.demo.dtos.userDtos.UserDto;
import com.booking_1.demo.dtos.userDtos.UserRegistrationDto;
import com.booking_1.demo.services.userServices.IUserService;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto save(@RequestBody UserRegistrationDto userRegistrationDto) {
        return userService.save(userRegistrationDto);
    }

    @PutMapping("/{id}")
    public UserDto update(@PathVariable UUID id, @RequestBody UserRegistrationDto dto) {
        return userService.updateUser(id, dto);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable UUID id) {
        return userService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }

}
