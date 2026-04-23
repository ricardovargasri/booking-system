
package com.booking_1.demo.spot.entities;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.booking_1.demo.core.enums.TypeSpot;
import com.booking_1.demo.user.entities.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
@EntityListeners(AuditingEntityListener.class)
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

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedAt;

}
