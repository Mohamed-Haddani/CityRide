package com.cityride.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Donnees modifiables du profil utilisateur.
 */
public record UpdateProfileRequest(
        @NotBlank @Size(max = 80) String firstName,
        @NotBlank @Size(max = 80) String lastName,
        @Size(max = 30) String phone,
        @Size(max = 80) String city,
        @Size(max = 500) String photoUrl
) {
}
