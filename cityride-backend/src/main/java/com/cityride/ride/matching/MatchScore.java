package com.cityride.ride.matching;

import java.util.Map;

/**
 * Resultat du calcul de compatibilite d'un trajet : score global (0-100)
 * et detail par critere (utile pour expliquer le classement cote UI).
 */
public record MatchScore(
        int total,
        Map<String, Integer> breakdown
) {
}
