package com.cityride.review.dto;

import com.cityride.user.dto.UserSummary;

import java.time.Instant;

public record ReviewResponse(
        Long id,
        UserSummary reviewer,
        int rating,
        String comment,
        Instant createdAt
) {
}
