package com.bookin.demo.service;

import com.bookin.demo.dto.RoomDto;
import com.bookin.demo.mapper.RoomMapper;
import com.bookin.demo.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    private final RoomMapper roomMapper;

    public List<RoomDto> findAll() {
        return roomRepository.findAll().stream()
                .map(roomMapper::toDto)
                .collect(Collectors.toList());
    }

    public RoomDto save(RoomDto roomDto) {
        var room = roomMapper.toEntity(roomDto);
        return roomMapper.toDto(roomRepository.save(room));
    }

    public RoomDto findById(Long id) {
        return roomRepository.findById(id)
                .map(roomMapper::toDto)
                .orElse(null);
    }
}
