package com.cityride.payment.provider;

/**
 * Resultat d'une tentative de paiement renvoyee par un fournisseur.
 */
public record PaymentResult(
        boolean success,
        String providerRef,
        String failureReason
) {
    public static PaymentResult ok(String providerRef) {
        return new PaymentResult(true, providerRef, null);
    }

    public static PaymentResult failed(String reason) {
        return new PaymentResult(false, null, reason);
    }
}
