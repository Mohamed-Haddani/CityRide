package com.cityride.review;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByRevieweeIdOrderByCreatedAtDesc(Long revieweeId);

    boolean existsByRideIdAndReviewerIdAndRevieweeId(Long rideId, Long reviewerId, Long revieweeId);
}
