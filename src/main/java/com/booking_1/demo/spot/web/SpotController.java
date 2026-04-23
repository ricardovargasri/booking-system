package com.booking_1.demo.spot.web;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
@Tag(name = "Spot", description = "Endpoints for managing accommodations (spots)")
public class SpotController {

    private final ISpotService spotService;

    @PostMapping
    @Operation(summary = "Register a new spot", description = "Adds a new accommodation to the platform")
    public SpotDto save(@RequestBody SpotRegistrationDto dto) {
        return spotService.save(dto);
    }

    @PutMapping("/{id}")
    public SpotDto update(@PathVariable Long id, @RequestBody SpotRegistrationDto dto) {
        return spotService.updateSpot(id, dto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find spot by ID", description = "Retrieves detailed information about a specific spot")
    public SpotDto findById(@PathVariable Long id) {
        return spotService.findById(id);
    }

    @GetMapping
    @Operation(summary = "Get all spots (Paginated)")
    public Page<SpotDto> getAllSpots(Pageable pageable) {
        return spotService.findAll(pageable);
    }

    @DeleteMapping("/{id}")
    public void deleteSpot(@PathVariable Long id) {
        spotService.deleteSpot(id);
    }

}
