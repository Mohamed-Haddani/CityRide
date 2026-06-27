package com.cityride.ride.matching;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Criteres de recherche d'un passager. Tous les champs sont optionnels
 * sauf minSeats (au moins 1). Utilise a la fois pour filtrer et pour scorer.
 */
public record SearchCriteria(
        String from,
        String to,
        LocalDateTime desiredTime,
        BigDecimal maxPrice,
        int minSeats,
        Double fromLat,
        Double fromLng,
        Double toLat,
        Double toLng
) {
    public boolean hasDepartureCoords() {
        return fromLat != null && fromLng != null;
    }

    public boolean hasArrivalCoords() {
        return toLat != null && toLng != null;
    }
}
