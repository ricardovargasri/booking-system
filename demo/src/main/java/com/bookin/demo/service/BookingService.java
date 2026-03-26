package com.bookin.demo.service;

import com.bookin.demo.dto.BookingDto;
import com.bookin.demo.entity.Booking;
import com.bookin.demo.entity.Room;
import com.bookin.demo.entity.User;
import com.bookin.demo.mapper.BookingMapper;
import com.bookin.demo.repository.BookingRepository;
import com.bookin.demo.repository.RoomRepository;
import com.bookin.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final RoomRepository roomRepository;

    private final BookingMapper bookingMapper;

    public List<BookingDto> findAll() {
        return bookingRepository.findAll().stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingDto createBooking(Long userId, Long roomId, BookingDto bookingRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        Booking booking = Booking.builder()
                .user(user)
                .room(room)
                .startTime(bookingRequest.startTime())
                .endTime(bookingRequest.endTime())
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        // Sincronización manual de la relación bidireccional en memoria
        user.getBookings().add(savedBooking);

        return bookingMapper.toDto(savedBooking);
    }
}
