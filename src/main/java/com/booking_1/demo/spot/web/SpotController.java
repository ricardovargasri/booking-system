package com.booking_1.demo.spot.web;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.booking_1.demo.spot.dtos.SpotDto;
import com.booking_1.demo.spot.dtos.SpotRegistrationDto;
import com.booking_1.demo.spot.services.ISpotService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/spot")
@RequiredArgsConstructor
public class SpotController {

    private final ISpotService spotService;

    @PostMapping
    public SpotDto save(@RequestBody SpotRegistrationDto dto) {
        return spotService.save(dto);
    }

    @PutMapping("/{id}")
    public SpotDto update(@PathVariable Long id, @RequestBody SpotRegistrationDto dto) {
        return spotService.updateSpot(id, dto);
    }

    @GetMapping("/{id}")
    public SpotDto getSpot(@PathVariable Long id) {
        return spotService.findById(id);
    }

    @GetMapping
    public List<SpotDto> getAllSpots() {
        return spotService.findAll();
    }

    @DeleteMapping("/{id}")
    public void deleteSpot(@PathVariable Long id) {
        spotService.deleteSpot(id);
    }

}
