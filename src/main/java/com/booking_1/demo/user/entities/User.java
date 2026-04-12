package com.booking_1.demo.user.entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.booking_1.demo.booking.entities.Booking;
import com.booking_1.demo.core.enums.Rol;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;

import jakarta.persistence.Table;

@Entity
@Data
@Table(name = "users")
public class User {

    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false)
    @Id
    private UUID id;

    @Column(updatable = false)
    private String name;

    @Column(updatable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    private String password;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(updatable = false)
    private LocalDateTime updateAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "guest")
    private List<Booking> bookings;
}
