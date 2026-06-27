package com.cityride.user.dto;

/**
 * Vue publique reduite d'un utilisateur (affichee sur les trajets, avis, etc.).
 */
public record UserSummary(
        Long id,
        String firstName,
        String lastName,
        String city,
        String photoUrl,
        double ratingAvg,
        int ratingCount
) {
}
