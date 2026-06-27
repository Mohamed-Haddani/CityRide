package com.cityride.review;

import com.cityride.review.dto.ReviewResponse;
import com.cityride.user.UserMapper;

public final class ReviewMapper {

    private ReviewMapper() {
    }

    public static ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                UserMapper.toSummary(review.getReviewer()),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt());
    }
}
