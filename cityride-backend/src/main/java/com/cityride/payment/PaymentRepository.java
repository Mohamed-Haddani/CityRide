package com.cityride.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByBookingId(Long bookingId);

    boolean existsByBookingIdAndStatus(Long bookingId, PaymentStatus status);

    /** Historique des paiements d'un passager (trajet charge pour l'affichage). */
    @Query("""
            select p from Payment p
            join fetch p.booking b
            join fetch b.ride
            where b.passenger.id = :userId
            order by p.createdAt desc
            """)
    List<Payment> findHistoryByUser(@Param("userId") Long userId);
}
