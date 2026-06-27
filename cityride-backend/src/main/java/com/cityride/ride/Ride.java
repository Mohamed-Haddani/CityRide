package com.cityride.ride;

import com.cityride.common.domain.BaseEntity;
import com.cityride.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Trajet propose par un conducteur.
 */
@Entity
@Table(name = "rides")
public class Ride extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "driver_id", nullable = false)
    private User driver;

    @Column(name = "departure_city", nullable = false)
    private String departureCity;

    @Column(name = "destination_city", nullable = false)
    private String destinationCity;

    @Column(name = "departure_point", nullable = false)
    private String departurePoint;

    @Column(name = "arrival_point", nullable = false)
    private String arrivalPoint;

    // Coordonnees optionnelles pour un matching geographique precis
    @Column(name = "departure_lat")
    private Double departureLat;
    @Column(name = "departure_lng")
    private Double departureLng;
    @Column(name = "arrival_lat")
    private Double arrivalLat;
    @Column(name = "arrival_lng")
    private Double arrivalLng;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name = "total_seats", nullable = false)
    private int totalSeats;

    @Column(name = "available_seats", nullable = false)
    private int availableSeats;

    @Column(name = "price_per_seat", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerSeat;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RideStatus status = RideStatus.ACTIVE;

    // --- Logique metier legere ---

    /** Diminue les places disponibles et passe le trajet en FULL si necessaire. */
    public void reserveSeats(int seats) {
        this.availableSeats -= seats;
        if (this.availableSeats <= 0) {
            this.availableSeats = 0;
            this.status = RideStatus.FULL;
        }
    }

    /** Restitue des places (annulation) et rouvre le trajet s'il etait complet. */
    public void releaseSeats(int seats) {
        this.availableSeats += seats;
        if (this.availableSeats > totalSeats) {
            this.availableSeats = totalSeats;
        }
        if (this.status == RideStatus.FULL && this.availableSeats > 0) {
            this.status = RideStatus.ACTIVE;
        }
    }

    // --- Getters / Setters ---

    public User getDriver() { return driver; }
    public void setDriver(User driver) { this.driver = driver; }

    public String getDepartureCity() { return departureCity; }
    public void setDepartureCity(String departureCity) { this.departureCity = departureCity; }

    public String getDestinationCity() { return destinationCity; }
    public void setDestinationCity(String destinationCity) { this.destinationCity = destinationCity; }

    public String getDeparturePoint() { return departurePoint; }
    public void setDeparturePoint(String departurePoint) { this.departurePoint = departurePoint; }

    public String getArrivalPoint() { return arrivalPoint; }
    public void setArrivalPoint(String arrivalPoint) { this.arrivalPoint = arrivalPoint; }

    public Double getDepartureLat() { return departureLat; }
    public void setDepartureLat(Double departureLat) { this.departureLat = departureLat; }

    public Double getDepartureLng() { return departureLng; }
    public void setDepartureLng(Double departureLng) { this.departureLng = departureLng; }

    public Double getArrivalLat() { return arrivalLat; }
    public void setArrivalLat(Double arrivalLat) { this.arrivalLat = arrivalLat; }

    public Double getArrivalLng() { return arrivalLng; }
    public void setArrivalLng(Double arrivalLng) { this.arrivalLng = arrivalLng; }

    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    public BigDecimal getPricePerSeat() { return pricePerSeat; }
    public void setPricePerSeat(BigDecimal pricePerSeat) { this.pricePerSeat = pricePerSeat; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public RideStatus getStatus() { return status; }
    public void setStatus(RideStatus status) { this.status = status; }
}
