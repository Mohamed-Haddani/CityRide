package com.cityride.ride;

public enum RideStatus {
    ACTIVE,      // trajet ouvert a la reservation
    FULL,        // plus de places disponibles
    CANCELLED,   // annule par le conducteur
    COMPLETED    // trajet effectue
}
