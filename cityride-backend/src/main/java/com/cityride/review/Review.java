package com.cityride.review;

import com.cityride.common.domain.BaseEntity;
import com.cityride.ride.Ride;
import com.cityride.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Avis laisse par un participant d'un trajet sur un autre participant.
 */
@Entity
@Table(name = "reviews", uniqueConstraints = @UniqueConstraint(
        name = "uk_review_ride_reviewer_reviewee",
        columnNames = {"ride_id", "reviewer_id", "reviewee_id"}))
public class Review extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewee_id", nullable = false)
    private User reviewee;

    @Column(nullable = false)
    private int rating;

    @Column(length = 500)
    private String comment;

    public Ride getRide() { return ride; }
    public void setRide(Ride ride) { this.ride = ride; }

    public User getReviewer() { return reviewer; }
    public void setReviewer(User reviewer) { this.reviewer = reviewer; }

    public User getReviewee() { return reviewee; }
    public void setReviewee(User reviewee) { this.reviewee = reviewee; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
