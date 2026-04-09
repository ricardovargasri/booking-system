package com.booking_1.demo.user.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.booking_1.demo.user.entities.User;

public interface UserRepository extends JpaRepository<User, UUID> {
}
