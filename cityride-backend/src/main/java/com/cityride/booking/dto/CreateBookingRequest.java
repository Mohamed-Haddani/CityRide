package com.cityride.booking.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateBookingRequest(
        @NotNull Long rideId,
        @Min(1) @Max(8) int seats
) {
}
