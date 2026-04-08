package com.booking_1.demo.repositories.userRepository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.booking_1.demo.entities.User;

public interface UserRepository extends JpaRepository<User, UUID> {
}
