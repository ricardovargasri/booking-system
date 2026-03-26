package com.bookin.demo.mapper;

import com.bookin.demo.dto.RoomDto;
import com.bookin.demo.entity.Room;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    RoomDto toDto(Room room);

    Room toEntity(RoomDto roomDto);
}
