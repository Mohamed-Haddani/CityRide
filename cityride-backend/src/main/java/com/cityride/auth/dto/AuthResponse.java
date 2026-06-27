package com.cityride.auth.dto;

import com.cityride.user.dto.UserResponse;

/**
 * Reponse renvoyee apres une inscription / connexion / rafraichissement reussi.
 */
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,        // duree de validite de l'access token (secondes)
        UserResponse user
) {
}
