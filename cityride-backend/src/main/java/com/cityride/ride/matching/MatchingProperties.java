package com.cityride.ride.matching;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Ponderations et seuils du moteur de matching (prefixe app.matching dans application.yml).
 * Externaliser ces valeurs permet d'ajuster l'algorithme sans recompiler.
 */
@ConfigurationProperties(prefix = "app.matching")
public record MatchingProperties(
        double weightLocation,
        double weightTime,
        double weightSeats,
        double weightPrice,
        double weightRating,
        double maxDistanceKm,
        double maxTimeDiffMinutes
) {
}
