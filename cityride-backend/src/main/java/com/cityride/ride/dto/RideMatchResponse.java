package com.cityride.ride.dto;

import java.util.Map;

/**
 * Resultat de recherche : un trajet enrichi de son score de compatibilite et du detail.
 */
public record RideMatchResponse(
        RideResponse ride,
        int matchScore,
        Map<String, Integer> scoreBreakdown
) {
}
