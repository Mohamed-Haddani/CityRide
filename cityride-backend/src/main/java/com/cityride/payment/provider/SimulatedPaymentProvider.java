package com.cityride.payment.provider;

import com.cityride.payment.PaymentProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Fournisseur de paiement simule : reussit toujours (sauf montant invalide).
 * Sert pour le MVP et les demos, en attendant l'integration de Stripe.
 */
@Component
public class SimulatedPaymentProvider implements PaymentProvider {

    private static final Logger log = LoggerFactory.getLogger(SimulatedPaymentProvider.class);

    @Override
    public PaymentProviderType type() {
        return PaymentProviderType.SIMULATED;
    }

    @Override
    public PaymentResult charge(BigDecimal amount, String currency, String description) {
        if (amount == null || amount.signum() < 0) {
            return PaymentResult.failed("Montant invalide");
        }
        String ref = "SIM-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        log.info("[PAIEMENT SIMULE] {} {} - {} -> {}", amount, currency, description, ref);
        return PaymentResult.ok(ref);
    }
}
