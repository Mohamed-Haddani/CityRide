package com.cityride.booking;

import com.cityride.booking.dto.BookingResponse;
import com.cityride.ride.RideMapper;
import com.cityride.user.UserMapper;

public final class BookingMapper {

    private BookingMapper() {
    }

    public static BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                RideMapper.toResponse(booking.getRide()),
                UserMapper.toSummary(booking.getPassenger()),
                booking.getSeatsBooked(),
                booking.getTotalPrice(),
                booking.getStatus(),
                booking.getCreatedAt());
    }
}
