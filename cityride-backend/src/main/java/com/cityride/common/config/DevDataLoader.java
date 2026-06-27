package com.cityride.common.config;

import com.cityride.ride.Ride;
import com.cityride.ride.RideRepository;
import com.cityride.ride.RideStatus;
import com.cityride.user.Role;
import com.cityride.user.User;
import com.cityride.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Cree des comptes ET des trajets de demonstration au demarrage, uniquement en profil "dev".
 * Identifiants : admin@cityride.com / admin123  -  alice@cityride.com / password123  -  bob@cityride.com / password123
 */
@Component
@Profile("dev")
public class DevDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataLoader.class);

    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final PasswordEncoder passwordEncoder;

    public DevDataLoader(UserRepository userRepository, RideRepository rideRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.rideRepository = rideRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // donnees deja presentes
        }
        createUser("admin@cityride.com", "admin123", "Admin", "CityRide", "Casablanca", Role.ADMIN, true);
        User alice = createUser("alice@cityride.com", "password123", "Alice", "Martin", "Casablanca", Role.USER, true);
        User bob = createUser("bob@cityride.com", "password123", "Bob", "Durand", "Rabat", Role.USER, false);

        // Coordonnees approximatives des centres-villes (pour le matching geographique)
        double[] casa = {33.5731, -7.5898};
        double[] rabat = {34.0209, -6.8417};
        double[] marrakech = {31.6295, -7.9811};

        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).withHour(8).withMinute(0).withSecond(0).withNano(0);

        createRide(alice, "Casablanca", "Rabat", "Gare Casa-Voyageurs", "Gare Rabat-Agdal",
                casa, rabat, tomorrow.with(LocalTime.of(8, 0)), 3, "50.00", "Depart ponctuel, musique au choix.");
        createRide(alice, "Casablanca", "Rabat", "Maarif", "Centre-ville Rabat",
                casa, rabat, tomorrow.with(LocalTime.of(18, 0)), 2, "45.00", "Retour en soiree.");
        createRide(bob, "Rabat", "Casablanca", "Hay Riad", "Casa Port",
                rabat, casa, tomorrow.with(LocalTime.of(9, 0)), 4, "60.00", "Vehicule confortable.");
        createRide(alice, "Casablanca", "Marrakech", "Sidi Maarouf", "Gueliz",
                casa, marrakech, tomorrow.plusDays(1).with(LocalTime.of(7, 30)), 3, "120.00", "Long trajet, pauses prevues.");

        log.info("[DEV] Donnees de demonstration creees : 3 utilisateurs, 4 trajets.");
    }

    private User createUser(String email, String rawPassword, String firstName, String lastName,
                            String city, Role role, boolean verified) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setCity(city);
        user.setRole(role);
        user.setVerified(verified);
        return userRepository.save(user);
    }

    private void createRide(User driver, String fromCity, String toCity, String fromPoint, String toPoint,
                            double[] fromCoords, double[] toCoords, LocalDateTime when, int seats,
                            String price, String description) {
        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setDepartureCity(fromCity);
        ride.setDestinationCity(toCity);
        ride.setDeparturePoint(fromPoint);
        ride.setArrivalPoint(toPoint);
        ride.setDepartureLat(fromCoords[0]);
        ride.setDepartureLng(fromCoords[1]);
        ride.setArrivalLat(toCoords[0]);
        ride.setArrivalLng(toCoords[1]);
        ride.setDepartureTime(when);
        ride.setTotalSeats(seats);
        ride.setAvailableSeats(seats);
        ride.setPricePerSeat(new BigDecimal(price));
        ride.setDescription(description);
        ride.setStatus(RideStatus.ACTIVE);
        rideRepository.save(ride);
    }
}
