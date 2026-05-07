package com.booking_1.demo.spot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booking_1.demo.spot.entities.Spot;

public interface SpotRepository extends JpaRepository<Spot, Long> {

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("SELECT s FROM Spot s WHERE s.id = :id")
    java.util.Optional<Spot> findByIdWithLock(@org.springframework.data.repository.query.Param("id") Long id);
}
