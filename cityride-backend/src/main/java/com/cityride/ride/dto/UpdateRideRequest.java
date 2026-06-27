package com.cityride.ride.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateRideRequest(
        @NotBlank @Size(max = 80) String departureCity,
        @NotBlank @Size(max = 80) String destinationCity,
        @NotBlank @Size(max = 150) String departurePoint,
        @NotBlank @Size(max = 150) String arrivalPoint,
        Double departureLat,
        Double departureLng,
        Double arrivalLat,
        Double arrivalLng,
        @NotNull @Future LocalDateTime departureTime,
        @Min(1) @Max(8) int totalSeats,
        @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal pricePerSeat,
        @Size(max = 1000) String description
) {
}
