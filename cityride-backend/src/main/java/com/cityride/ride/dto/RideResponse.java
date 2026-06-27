package com.cityride.ride.dto;

import com.cityride.ride.RideStatus;
import com.cityride.user.dto.UserSummary;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

public record RideResponse(
        Long id,
        UserSummary driver,
        String departureCity,
        String destinationCity,
        String departurePoint,
        String arrivalPoint,
        Double departureLat,
        Double departureLng,
        Double arrivalLat,
        Double arrivalLng,
        LocalDateTime departureTime,
        int totalSeats,
        int availableSeats,
        BigDecimal pricePerSeat,
        String description,
        RideStatus status,
        Instant createdAt
) {
}
