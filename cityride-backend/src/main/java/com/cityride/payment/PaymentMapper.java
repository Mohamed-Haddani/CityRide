package com.cityride.payment;

import com.cityride.payment.dto.PaymentResponse;
import com.cityride.ride.Ride;

public final class PaymentMapper {

    private PaymentMapper() {
    }

    public static PaymentResponse toResponse(Payment payment) {
        Ride ride = payment.getBooking().getRide();
        String label = ride.getDepartureCity() + " -> " + ride.getDestinationCity();
        return new PaymentResponse(
                payment.getId(),
                payment.getBooking().getId(),
                label,
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getProvider(),
                payment.getProviderRef(),
                payment.getPaidAt(),
                payment.getCreatedAt());
    }
}
