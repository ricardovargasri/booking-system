package com.booking_1.demo.booking.mappers;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.booking_1.demo.booking.dtos.BookingDto;
import com.booking_1.demo.booking.dtos.BookingRegistrationDto;
import com.booking_1.demo.booking.entities.Booking;
import com.booking_1.demo.core.enums.BookingStatus;
import com.booking_1.demo.core.enums.PaymentStatus;
import com.booking_1.demo.spot.entities.Spot;
import com.booking_1.demo.user.entities.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
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
