package com.booking_1.demo.repositories.spotRepository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booking_1.demo.spot.entities.Spot;

public interface SpotRepository extends JpaRepository<Spot, Long> {
}
