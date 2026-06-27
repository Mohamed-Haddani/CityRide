package com.cityride.ride;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {

    List<Ride> findByDriverIdOrderByDepartureTimeDesc(Long driverId);

    /**
     * Trajets candidats pour une recherche : actifs, futurs, avec assez de places,
     * filtres optionnellement par ville, fenetre de date et prix max.
     * Le tri fin par score de compatibilite est fait ensuite par le MatchingService.
     */
    @Query("""
            select r from Ride r
            join fetch r.driver
            where r.status = com.cityride.ride.RideStatus.ACTIVE
              and r.departureTime >= :now
              and r.availableSeats >= :minSeats
              and (:from is null or lower(r.departureCity) like lower(concat('%', :from, '%')))
              and (:to is null or lower(r.destinationCity) like lower(concat('%', :to, '%')))
              and (:dateStart is null or r.departureTime >= :dateStart)
              and (:dateEnd is null or r.departureTime < :dateEnd)
              and (:maxPrice is null or r.pricePerSeat <= :maxPrice)
            """)
    List<Ride> search(@Param("now") LocalDateTime now,
                      @Param("minSeats") int minSeats,
                      @Param("from") String from,
                      @Param("to") String to,
                      @Param("dateStart") LocalDateTime dateStart,
                      @Param("dateEnd") LocalDateTime dateEnd,
                      @Param("maxPrice") BigDecimal maxPrice);
}
