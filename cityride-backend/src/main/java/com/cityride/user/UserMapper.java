package com.cityride.user;

import com.cityride.user.dto.UserResponse;
import com.cityride.user.dto.UserSummary;

/**
 * Conversion entite User <-> DTOs. Mappers explicites (pas de dependance a un processeur d'annotations).
 */
public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getCity(),
                user.getPhotoUrl(),
                user.getRole(),
                user.getRatingAvg(),
                user.getRatingCount(),
                user.isVerified(),
                user.isBlocked());
    }

    public static UserSummary toSummary(User user) {
        return new UserSummary(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getCity(),
                user.getPhotoUrl(),
                user.getRatingAvg(),
                user.getRatingCount());
    }
}
