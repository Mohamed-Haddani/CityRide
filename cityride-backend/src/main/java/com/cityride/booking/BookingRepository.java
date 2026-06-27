package com.cityride.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByPassengerIdOrderByCreatedAtDesc(Long passengerId);

    List<Booking> findByRideIdOrderByCreatedAtDesc(Long rideId);

    boolean existsByRideIdAndPassengerId(Long rideId, Long passengerId);
}
