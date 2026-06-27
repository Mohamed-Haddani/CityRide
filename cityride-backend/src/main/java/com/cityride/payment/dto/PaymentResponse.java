package com.cityride.payment.dto;

import com.cityride.payment.PaymentProviderType;
import com.cityride.payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        Long id,
        Long bookingId,
        String label,              // ex : "Casablanca -> Rabat" (pour l'historique)
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        PaymentProviderType provider,
        String providerRef,
        Instant paidAt,
        Instant createdAt
) {
}
