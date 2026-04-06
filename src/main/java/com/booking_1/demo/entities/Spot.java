
package com.booking_1.demo.entities;

import com.booking_1.demo.enums.TypeSpot;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Spot {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    @Id
    private Long id;

    private String name;

    private String location;

    @Column(name = "night_price")
    private Double pricePerNight;

    @Column(name = "max_capacity")
    private Integer maxCapacity;

    @Column(name = "type_spot")
    private TypeSpot typeSpot;

    @Column(name = "is_available")
    private Boolean isAvailable;

    @ManyToOne
    private User owner;
}
