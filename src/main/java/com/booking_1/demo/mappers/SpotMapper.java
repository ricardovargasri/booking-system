package com.booking_1.demo.mappers;

import java.util.Optional;

import com.booking_1.demo.dtos.spotDtos.SpotRegistrationDto;
import com.booking_1.demo.dtos.spotDtos.SpotDto;
import com.booking_1.demo.entities.Spot;

public class SpotMapper {

    public static SpotDto toDto(Spot spot) {
        return Optional.ofNullable(spot)
                .map(s -> new SpotDto(
                        s.getName(),
                        s.getLocation(),
                        s.getPricePerNight(),
                        s.getMaxCapacity(),
                        s.getTypeSpot()))
                .orElse(null);

    }

    public static Spot spotToEntity(SpotRegistrationDto spotRegistrationDto) {
        return Optional.ofNullable(spotRegistrationDto)
                .map(s -> {
                    Spot spot = new Spot();
                    spot.setName(s.name());
                    spot.setLocation(s.location());
                    spot.setPricePerNight(s.pricePerNight());
                    spot.setMaxCapacity(s.maxCapacity());
                    spot.setTypeSpot(s.typeSpot());
                    return spot;
                })
                .orElse(null);
    }
}
