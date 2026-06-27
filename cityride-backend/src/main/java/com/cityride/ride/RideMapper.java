package com.cityride.ride;

import com.cityride.ride.dto.RideResponse;
import com.cityride.user.UserMapper;

public final class RideMapper {

    private RideMapper() {
    }

    public static RideResponse toResponse(Ride ride) {
        return new RideResponse(
                ride.getId(),
                UserMapper.toSummary(ride.getDriver()),
                ride.getDepartureCity(),
                ride.getDestinationCity(),
                ride.getDeparturePoint(),
                ride.getArrivalPoint(),
                ride.getDepartureLat(),
                ride.getDepartureLng(),
                ride.getArrivalLat(),
                ride.getArrivalLng(),
                ride.getDepartureTime(),
                ride.getTotalSeats(),
                ride.getAvailableSeats(),
                ride.getPricePerSeat(),
                ride.getDescription(),
                ride.getStatus(),
                ride.getCreatedAt());
    }
}
