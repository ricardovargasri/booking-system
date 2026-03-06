package com.bookin.demo;

import com.bookin.demo.dto.BookingDto;
import com.bookin.demo.entity.Room;
import com.bookin.demo.entity.User;
import com.bookin.demo.repository.RoomRepository;
import com.bookin.demo.repository.UserRepository;
import com.bookin.demo.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class BookingPersistenceTest {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private RoomRepository roomRepository;

        @Autowired
        private BookingService bookingService;

        @Test
        @Transactional
        void testBookingFlow() {
                // 1. Create User
                User user = User.builder()
                                .name("John Doe")
                                .email("john@example.com")
                                .password("password123")
                                .build();
                user = userRepository.save(user);

                // 2. Create Room
                Room room = Room.builder()
                                .name("Deluxe Suite")
                                .capacity(2)
                                .build();
                room = roomRepository.save(room);

                // 3. Create Booking via Service (using DTO)
                LocalDateTime startTime = LocalDateTime.now();
                LocalDateTime endTime = LocalDateTime.now().plusDays(2);
                BookingDto bookingRequest = new BookingDto(null, null, null, startTime, endTime);

                BookingDto savedBooking = bookingService.createBooking(user.getId(), room.getId(), bookingRequest);

                // 4. Verify
                assertThat(savedBooking.id()).isNotNull();
                assertThat(savedBooking.user().name()).isEqualTo("John Doe");
                assertThat(savedBooking.room().name()).isEqualTo("Deluxe Suite");

                // 5. Verify Bidirectional (Service should have handled this)
                User savedUser = userRepository.findById(user.getId()).orElseThrow();
                assertThat(savedUser.getBookings()).hasSize(1);
                assertThat(savedUser.getBookings().get(0).getRoom().getName()).isEqualTo("Deluxe Suite");

                System.out.println(
                                "Service-based booking flow verified. User has " + savedUser.getBookings().size()
                                                + " bookings.");
        }
}
