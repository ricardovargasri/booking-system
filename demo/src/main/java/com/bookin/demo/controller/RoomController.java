package com.bookin.demo.controller;

import com.bookin.demo.dto.RoomDto;
import com.bookin.demo.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public List<RoomDto> getAllRooms() {
        return roomService.findAll();
    }

    @PostMapping
    public RoomDto createRoom(@RequestBody RoomDto roomDto) {
        return roomService.save(roomDto);
    }
}
