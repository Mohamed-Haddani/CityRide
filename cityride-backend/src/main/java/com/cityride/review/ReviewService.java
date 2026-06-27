package com.cityride.review;

import com.cityride.booking.BookingRepository;
import com.cityride.common.exception.BusinessException;
import com.cityride.common.exception.ConflictException;
import com.cityride.notification.NotificationService;
import com.cityride.notification.NotificationType;
import com.cityride.review.dto.CreateReviewRequest;
import com.cityride.review.dto.ReviewResponse;
import com.cityride.ride.Ride;
import com.cityride.ride.RideService;
import com.cityride.user.User;
import com.cityride.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RideService rideService;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final NotificationService notificationService;

    public ReviewService(ReviewRepository reviewRepository, RideService rideService, UserService userService,
                         BookingRepository bookingRepository, NotificationService notificationService) {
        this.reviewRepository = reviewRepository;
        this.rideService = rideService;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public ReviewResponse create(Long reviewerId, CreateReviewRequest req) {
        if (reviewerId.equals(req.revieweeId())) {
            throw new BusinessException("Vous ne pouvez pas vous noter vous-meme");
        }
        Ride ride = rideService.getEntity(req.rideId());
        User reviewer = userService.getEntity(reviewerId);
        User reviewee = userService.getEntity(req.revieweeId());

        if (!isParticipant(ride, reviewerId)) {
            throw new BusinessException("Vous n'avez pas participe a ce trajet");
        }
        if (!isParticipant(ride, req.revieweeId())) {
            throw new BusinessException("La personne notee n'a pas participe a ce trajet");
        }
        if (reviewRepository.existsByRideIdAndReviewerIdAndRevieweeId(ride.getId(), reviewerId, req.revieweeId())) {
            throw new ConflictException("Vous avez deja note cette personne pour ce trajet");
        }

        Review review = new Review();
        review.setRide(ride);
        review.setReviewer(reviewer);
        review.setReviewee(reviewee);
        review.setRating(req.rating());
        review.setComment(req.comment());
        Review saved = reviewRepository.save(review);

        applyRating(reviewee, req.rating());
        notificationService.notify(reviewee, NotificationType.REVIEW_RECEIVED,
                reviewer.getFirstName() + " vous a laisse un avis (" + req.rating() + "/5)", saved.getId());

        return ReviewMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getForUser(Long userId) {
        return reviewRepository.findByRevieweeIdOrderByCreatedAtDesc(userId).stream()
                .map(ReviewMapper::toResponse)
                .toList();
    }

    private boolean isParticipant(Ride ride, Long userId) {
        return ride.getDriver().getId().equals(userId)
                || bookingRepository.existsByRideIdAndPassengerId(ride.getId(), userId);
    }

    /** Recalcule la moyenne incrementale de l'utilisateur note. */
    private void applyRating(User reviewee, int rating) {
        int newCount = reviewee.getRatingCount() + 1;
        double newAvg = (reviewee.getRatingAvg() * reviewee.getRatingCount() + rating) / newCount;
        reviewee.setRatingCount(newCount);
        reviewee.setRatingAvg(Math.round(newAvg * 100.0) / 100.0);
    }
}
