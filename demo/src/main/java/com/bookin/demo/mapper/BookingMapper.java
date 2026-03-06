package com.bookin.demo.mapper;

import com.bookin.demo.dto.BookingDto;
import com.bookin.demo.entity.Booking;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { UserMapper.class, RoomMapper.class })
public interface BookingMapper {
    BookingDto toDto(Booking booking);

    Booking toEntity(BookingDto bookingDto);
}
