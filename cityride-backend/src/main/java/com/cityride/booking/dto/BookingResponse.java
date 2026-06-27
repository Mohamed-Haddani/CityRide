package com.cityride.booking.dto;

import com.cityride.booking.BookingStatus;
import com.cityride.ride.dto.RideResponse;
import com.cityride.user.dto.UserSummary;

import java.math.BigDecimal;
import java.time.Instant;

public record BookingResponse(
        Long id,
        RideResponse ride,
        UserSummary passenger,
        int seatsBooked,
        BigDecimal totalPrice,
        BookingStatus status,
        Instant createdAt
) {
}
