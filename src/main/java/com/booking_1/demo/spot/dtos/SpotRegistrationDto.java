package com.booking_1.demo.spot.dtos;

import com.booking_1.demo.core.enums.TypeSpot;
import jakarta.validation.constraints.*;

public record SpotRegistrationDto(
        @NotBlank(message = "El nombre no puede estar vacío")
        @Size(max = 100, message = "El nombre es demasiado largo")
        String name,

        @NotBlank(message = "La ubicación es obligatoria")
        String location,

        @NotNull(message = "El precio es obligatorio")
        @Min(value = 20, message = "El precio mínimo es 20")
        @Max(value = 10000, message = "El precio máximo es 10000")
        Double pricePerNight,

        @NotNull(message = "La capacidad es obligatoria")
        @Min(value = 1, message = "La capacidad mínima es 1")
        @Max(value = 50, message = "La capacidad máxima es 50")
        Integer maxCapacity,

        @NotNull(message = "El tipo de alojamiento es obligatorio")
        TypeSpot typeSpot) {

}
