package com.booking_1.demo.spot.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.booking_1.demo.spot.dtos.SpotRegistrationDto;
import com.booking_1.demo.spot.dtos.SpotDto;
import com.booking_1.demo.spot.entities.Spot;

@Mapper(componentModel = "spring")
public interface SpotMapper {

    SpotDto toDto(Spot spot);

    @Mapping(target = "owner", ignore = true)
    Spot spotToEntity(SpotRegistrationDto spotRegistrationDto);
}
