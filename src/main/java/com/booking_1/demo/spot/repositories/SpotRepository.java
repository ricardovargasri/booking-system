package com.booking_1.demo.spot.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;

import com.booking_1.demo.spot.entities.Spot;

import jakarta.persistence.LockModeType;

public interface SpotRepository extends JpaRepository<Spot, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Spot s WHERE s.id = :id")
    Optional<Spot> findByIdWithLock(@Param("id") Long id);

    Page<Spot> findByOwnerId(UUID ownerId, Pageable pageable);

}
