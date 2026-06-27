package com.cityride.payment.provider;

import com.cityride.payment.PaymentProviderType;

import java.math.BigDecimal;

/**
 * Abstraction d'un fournisseur de paiement. Permet de brancher Stripe (ou autre)
 * plus tard sans modifier la logique metier : il suffit d'ajouter une implementation.
 */
public interface PaymentProvider {

    PaymentProviderType type();

    PaymentResult charge(BigDecimal amount, String currency, String description);
}
