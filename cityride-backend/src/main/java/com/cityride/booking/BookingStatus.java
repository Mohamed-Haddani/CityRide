package com.cityride.booking;

public enum BookingStatus {
    PENDING,     // en attente de confirmation du conducteur
    CONFIRMED,   // acceptee par le conducteur, en attente de paiement
    CANCELLED,   // annulee (par le passager ou refusee par le conducteur)
    PAID         // payee
}
