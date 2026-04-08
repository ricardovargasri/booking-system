package com.booking_1.demo.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import com.booking_1.demo.enums.Rol;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;

@Entity
@Data

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

}
