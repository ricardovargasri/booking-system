package com.booking_1.demo.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.booking_1.demo.dtos.bookingDtos.BookingDto;
import com.booking_1.demo.dtos.bookingDtos.BookingRegistrationDto;
import com.booking_1.demo.entities.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "guestId", source = "guest.id")
    @Mapping(target = "spotId", source = "spot.id")
    BookingDto toDto(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "guest", ignore = true)
    @Mapping(target = "spot", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "paymentStatus", ignore = true)
    Booking toEntity(BookingRegistrationDto registrationDto);

}
