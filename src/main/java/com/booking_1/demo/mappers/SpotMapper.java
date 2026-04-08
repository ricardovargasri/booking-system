package com.booking_1.demo.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.booking_1.demo.dtos.spotDtos.SpotRegistrationDto;
import com.booking_1.demo.dtos.spotDtos.SpotDto;
import com.booking_1.demo.entities.Spot;

@Mapper(componentModel = "spring")
public interface SpotMapper {

    SpotDto toDto(Spot spot);

    @Mapping(target = "owner", ignore = true)
    Spot spotToEntity(SpotRegistrationDto spotRegistrationDto);
}
