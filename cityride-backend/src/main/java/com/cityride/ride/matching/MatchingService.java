package com.cityride.ride.matching;

import com.cityride.ride.Ride;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Calcule un score de compatibilite (0-100) entre un trajet et les criteres de recherche.
 *
 * score = wLieu*proximite + wHoraire*compatibiliteHoraire + wPlaces*disponibilite
 *       + wPrix*prix + wNote*noteConducteur
 *
 * Chaque sous-score est dans [0, 100] ; les ponderations sont configurables (application.yml).
 */
@Service
public class MatchingService {

    private static final int NEUTRAL = 100;          // sous-score quand le critere n'est pas exprime
    private static final int NEW_DRIVER_RATING = 60; // note neutre pour un conducteur sans avis
    private static final double EARTH_RADIUS_KM = 6371.0;

    private final MatchingProperties props;

    public MatchingService(MatchingProperties props) {
        this.props = props;
    }

    public MatchScore score(Ride ride, SearchCriteria criteria) {
        int location = (int) Math.round(locationScore(ride, criteria));
        int time = (int) Math.round(timeScore(ride, criteria));
        int seats = (int) Math.round(seatsScore(ride, criteria));
        int price = (int) Math.round(priceScore(ride, criteria));
        int rating = (int) Math.round(ratingScore(ride));

        double total = location * props.weightLocation()
                + time * props.weightTime()
                + seats * props.weightSeats()
                + price * props.weightPrice()
                + rating * props.weightRating();

        Map<String, Integer> breakdown = new LinkedHashMap<>();
        breakdown.put("location", location);
        breakdown.put("time", time);
        breakdown.put("seats", seats);
        breakdown.put("price", price);
        breakdown.put("rating", rating);

        return new MatchScore((int) Math.round(clamp(total)), breakdown);
    }

    // --- Proximite des lieux (depart + arrivee) ---
    private double locationScore(Ride ride, SearchCriteria c) {
        double departure = subLocationScore(c.fromLat(), c.fromLng(), ride.getDepartureLat(), ride.getDepartureLng(),
                c.hasDepartureCoords(), c.from(), ride.getDepartureCity());
        double arrival = subLocationScore(c.toLat(), c.toLng(), ride.getArrivalLat(), ride.getArrivalLng(),
                c.hasArrivalCoords(), c.to(), ride.getDestinationCity());
        return (departure + arrival) / 2.0;
    }

    private double subLocationScore(Double critLat, Double critLng, Double rideLat, Double rideLng,
                                    boolean critHasCoords, String critCity, String rideCity) {
        if (critHasCoords && rideLat != null && rideLng != null) {
            double km = haversineKm(critLat, critLng, rideLat, rideLng);
            return distanceToScore(km);
        }
        if (critCity != null && !critCity.isBlank()) {
            // Les candidats ont deja passe le filtre ville : forte affinite si la ville contient le terme.
            return rideCity != null && rideCity.toLowerCase().contains(critCity.toLowerCase()) ? 100 : 40;
        }
        return NEUTRAL; // aucune contrainte de lieu exprimee
    }

    private double distanceToScore(double km) {
        if (km >= props.maxDistanceKm()) return 0;
        return 100.0 * (1.0 - km / props.maxDistanceKm());
    }

    // --- Compatibilite horaire ---
    private double timeScore(Ride ride, SearchCriteria c) {
        if (c.desiredTime() == null) return NEUTRAL;
        long diffMin = Math.abs(Duration.between(c.desiredTime(), ride.getDepartureTime()).toMinutes());
        if (diffMin >= props.maxTimeDiffMinutes()) return 0;
        return 100.0 * (1.0 - diffMin / props.maxTimeDiffMinutes());
    }

    // --- Disponibilite des places (preference legere pour plus de places) ---
    private double seatsScore(Ride ride, SearchCriteria c) {
        int extra = ride.getAvailableSeats() - c.minSeats();
        if (extra < 0) return 0;
        return clamp(80 + 5.0 * extra);
    }

    // --- Prix (moins cher = meilleur) ---
    private double priceScore(Ride ride, SearchCriteria c) {
        double price = ride.getPricePerSeat().doubleValue();
        if (c.maxPrice() != null) {
            double max = c.maxPrice().doubleValue();
            if (max <= 0) return NEUTRAL;
            // sous le budget : de 100 (gratuit) a 50 (au plafond)
            return clamp(100.0 - 50.0 * (price / max));
        }
        // sans budget : score relatif, decroissant avec le prix (10 = prix de reference)
        return clamp(100.0 * (10.0 / (10.0 + price)));
    }

    // --- Note du conducteur ---
    private double ratingScore(Ride ride) {
        if (ride.getDriver().getRatingCount() == 0) return NEW_DRIVER_RATING;
        return ride.getDriver().getRatingAvg() / 5.0 * 100.0;
    }

    private double clamp(double value) {
        return Math.max(0, Math.min(100, value));
    }

    /** Distance du grand cercle entre deux points GPS (formule de Haversine), en km. */
    private double haversineKm(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
