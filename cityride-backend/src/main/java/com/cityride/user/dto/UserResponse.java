package com.cityride.user.dto;

import com.cityride.user.Role;

/**
 * Vue complete d'un utilisateur (pour soi-meme ou un admin).
 */
public record UserResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        String phone,
        String city,
        String photoUrl,
        Role role,
        double ratingAvg,
        int ratingCount,
        boolean verified,
        boolean blocked
) {
}
