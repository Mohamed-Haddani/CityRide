package com.cityride.ride;

import com.cityride.common.exception.BusinessException;
import com.cityride.common.exception.ResourceNotFoundException;
import com.cityride.ride.dto.CreateRideRequest;
import com.cityride.ride.dto.RideMatchResponse;
import com.cityride.ride.dto.RideResponse;
import com.cityride.ride.dto.UpdateRideRequest;
import com.cityride.ride.matching.MatchScore;
import com.cityride.ride.matching.MatchingService;
import com.cityride.ride.matching.SearchCriteria;
import com.cityride.user.User;
import com.cityride.user.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final UserService userService;
    private final MatchingService matchingService;

    public RideService(RideRepository rideRepository, UserService userService, MatchingService matchingService) {
        this.rideRepository = rideRepository;
        this.userService = userService;
        this.matchingService = matchingService;
    }

    /** Recupere l'entite (avec verrou metier ailleurs). Reutilisee par le module reservation. */
    @Transactional(readOnly = true)
    public Ride getEntity(Long id) {
        return rideRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Trajet", id));
    }

    @Transactional(readOnly = true)
    public RideResponse getById(Long id) {
        return RideMapper.toResponse(getEntity(id));
    }

    @Transactional(readOnly = true)
    public List<RideResponse> getMyRides(Long driverId) {
        return rideRepository.findByDriverIdOrderByDepartureTimeDesc(driverId).stream()
                .map(RideMapper::toResponse)
                .toList();
    }

    @Transactional
    public RideResponse create(Long driverId, CreateRideRequest req) {
        User driver = userService.getEntity(driverId);
        Ride ride = new Ride();
        ride.setDriver(driver);
        applyFields(ride, req.departureCity(), req.destinationCity(), req.departurePoint(), req.arrivalPoint(),
                req.departureLat(), req.departureLng(), req.arrivalLat(), req.arrivalLng(),
                req.departureTime(), req.totalSeats(), req.pricePerSeat(), req.description());
        ride.setAvailableSeats(req.totalSeats());
        ride.setStatus(RideStatus.ACTIVE);
        return RideMapper.toResponse(rideRepository.save(ride));
    }

    @Transactional
    public RideResponse update(Long userId, Long rideId, UpdateRideRequest req) {
        Ride ride = getEntity(rideId);
        checkOwner(ride, userId);
        if (ride.getStatus() == RideStatus.CANCELLED || ride.getStatus() == RideStatus.COMPLETED) {
            throw new BusinessException("Ce trajet ne peut plus etre modifie");
        }
        int bookedSeats = ride.getTotalSeats() - ride.getAvailableSeats();
        if (req.totalSeats() < bookedSeats) {
            throw new BusinessException("Le nombre de places ne peut pas etre inferieur aux reservations deja faites ("
                    + bookedSeats + ")");
        }
        applyFields(ride, req.departureCity(), req.destinationCity(), req.departurePoint(), req.arrivalPoint(),
                req.departureLat(), req.departureLng(), req.arrivalLat(), req.arrivalLng(),
                req.departureTime(), req.totalSeats(), req.pricePerSeat(), req.description());
        ride.setAvailableSeats(req.totalSeats() - bookedSeats);
        // Rouvre le trajet s'il restait des places apres ajustement
        if (ride.getStatus() == RideStatus.FULL && ride.getAvailableSeats() > 0) {
            ride.setStatus(RideStatus.ACTIVE);
        }
        return RideMapper.toResponse(rideRepository.save(ride));
    }

    @Transactional
    public void cancel(Long userId, Long rideId) {
        Ride ride = getEntity(rideId);
        checkOwner(ride, userId);
        ride.setStatus(RideStatus.CANCELLED);
        rideRepository.save(ride);
    }

    @Transactional(readOnly = true)
    public List<RideMatchResponse> search(SearchCriteria criteria) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dateStart = null;
        LocalDateTime dateEnd = null;
        if (criteria.desiredTime() != null) {
            dateStart = criteria.desiredTime().toLocalDate().atStartOfDay();
            dateEnd = dateStart.plusDays(1);
        }
        int minSeats = Math.max(1, criteria.minSeats());

        List<Ride> candidates = rideRepository.search(
                now, minSeats, criteria.from(), criteria.to(), dateStart, dateEnd, criteria.maxPrice());

        return candidates.stream()
                .map(ride -> {
                    MatchScore score = matchingService.score(ride, criteria);
                    return new RideMatchResponse(RideMapper.toResponse(ride), score.total(), score.breakdown());
                })
                .sorted(Comparator.comparingInt(RideMatchResponse::matchScore).reversed())
                .toList();
    }

    private void checkOwner(Ride ride, Long userId) {
        if (!ride.getDriver().getId().equals(userId)) {
            throw new AccessDeniedException("Vous n'etes pas le conducteur de ce trajet");
        }
    }

    private void applyFields(Ride ride, String departureCity, String destinationCity, String departurePoint,
                             String arrivalPoint, Double depLat, Double depLng, Double arrLat, Double arrLng,
                             LocalDateTime departureTime, int totalSeats, java.math.BigDecimal price, String description) {
        ride.setDepartureCity(departureCity);
        ride.setDestinationCity(destinationCity);
        ride.setDeparturePoint(departurePoint);
        ride.setArrivalPoint(arrivalPoint);
        ride.setDepartureLat(depLat);
        ride.setDepartureLng(depLng);
        ride.setArrivalLat(arrLat);
        ride.setArrivalLng(arrLng);
        ride.setDepartureTime(departureTime);
        ride.setTotalSeats(totalSeats);
        ride.setPricePerSeat(price);
        ride.setDescription(description);
    }
}
