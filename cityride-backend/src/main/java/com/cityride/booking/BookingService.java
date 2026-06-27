package com.cityride.booking;

import com.cityride.booking.dto.BookingResponse;
import com.cityride.booking.dto.CreateBookingRequest;
import com.cityride.common.exception.BusinessException;
import com.cityride.common.exception.ConflictException;
import com.cityride.common.exception.ResourceNotFoundException;
import com.cityride.notification.NotificationService;
import com.cityride.notification.NotificationType;
import com.cityride.ride.Ride;
import com.cityride.ride.RideRepository;
import com.cityride.ride.RideService;
import com.cityride.ride.RideStatus;
import com.cityride.user.User;
import com.cityride.user.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RideService rideService;
    private final RideRepository rideRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    public BookingService(BookingRepository bookingRepository, RideService rideService,
                          RideRepository rideRepository, UserService userService,
                          NotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.rideService = rideService;
        this.rideRepository = rideRepository;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    private static String route(Ride ride) {
        return ride.getDepartureCity() + " -> " + ride.getDestinationCity();
    }

    @Transactional(readOnly = true)
    public Booking getEntity(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Reservation", id));
    }

    @Transactional
    public BookingResponse create(Long passengerId, CreateBookingRequest req) {
        Ride ride = rideService.getEntity(req.rideId());

        if (ride.getDriver().getId().equals(passengerId)) {
            throw new BusinessException("Vous ne pouvez pas reserver votre propre trajet");
        }
        if (ride.getStatus() != RideStatus.ACTIVE) {
            throw new BusinessException("Ce trajet n'est pas disponible a la reservation");
        }
        if (req.seats() > ride.getAvailableSeats()) {
            throw new BusinessException("Places insuffisantes (" + ride.getAvailableSeats() + " disponibles)");
        }
        if (bookingRepository.existsByRideIdAndPassengerId(ride.getId(), passengerId)) {
            throw new ConflictException("Vous avez deja une reservation sur ce trajet");
        }

        User passenger = userService.getEntity(passengerId);
        Booking booking = new Booking();
        booking.setRide(ride);
        booking.setPassenger(passenger);
        booking.setSeatsBooked(req.seats());
        booking.setTotalPrice(ride.getPricePerSeat().multiply(BigDecimal.valueOf(req.seats())));
        booking.setStatus(BookingStatus.PENDING);

        // Les places sont reservees immediatement pour eviter la surreservation.
        ride.reserveSeats(req.seats());
        rideRepository.save(ride);

        Booking saved = bookingRepository.save(booking);
        notificationService.notify(ride.getDriver(), NotificationType.BOOKING_CREATED,
                passenger.getFirstName() + " a reserve " + req.seats() + " place(s) sur " + route(ride),
                saved.getId());
        return BookingMapper.toResponse(saved);
    }

    /** Detail d'une reservation, accessible au passager concerne ou au conducteur du trajet. */
    @Transactional(readOnly = true)
    public BookingResponse getOne(Long userId, Long bookingId) {
        Booking booking = getEntity(bookingId);
        boolean isPassenger = booking.getPassenger().getId().equals(userId);
        boolean isOwner = booking.getRide().getDriver().getId().equals(userId);
        if (!isPassenger && !isOwner) {
            throw new AccessDeniedException("Acces refuse a cette reservation");
        }
        return BookingMapper.toResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(Long passengerId) {
        return bookingRepository.findByPassengerIdOrderByCreatedAtDesc(passengerId).stream()
                .map(BookingMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getRideBookings(Long ownerId, Long rideId) {
        Ride ride = rideService.getEntity(rideId);
        if (!ride.getDriver().getId().equals(ownerId)) {
            throw new AccessDeniedException("Vous n'etes pas le conducteur de ce trajet");
        }
        return bookingRepository.findByRideIdOrderByCreatedAtDesc(rideId).stream()
                .map(BookingMapper::toResponse)
                .toList();
    }

    /** Le conducteur accepte une reservation en attente. */
    @Transactional
    public BookingResponse accept(Long ownerId, Long bookingId) {
        Booking booking = loadAsOwner(ownerId, bookingId);
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BusinessException("Seules les reservations en attente peuvent etre acceptees");
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        Booking saved = bookingRepository.save(booking);
        notificationService.notify(saved.getPassenger(), NotificationType.BOOKING_ACCEPTED,
                "Votre reservation " + route(saved.getRide()) + " a ete acceptee", saved.getId());
        return BookingMapper.toResponse(saved);
    }

    /** Le conducteur refuse une reservation : les places sont restituees. */
    @Transactional
    public BookingResponse reject(Long ownerId, Long bookingId) {
        Booking booking = loadAsOwner(ownerId, bookingId);
        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.PAID) {
            throw new BusinessException("Cette reservation ne peut plus etre refusee");
        }
        releaseAndCancel(booking);
        Booking saved = bookingRepository.save(booking);
        notificationService.notify(saved.getPassenger(), NotificationType.BOOKING_REJECTED,
                "Votre reservation " + route(saved.getRide()) + " a ete refusee", saved.getId());
        return BookingMapper.toResponse(saved);
    }

    /** Le passager annule sa propre reservation (sauf si deja payee). */
    @Transactional
    public BookingResponse cancel(Long passengerId, Long bookingId) {
        Booking booking = getEntity(bookingId);
        if (!booking.getPassenger().getId().equals(passengerId)) {
            throw new AccessDeniedException("Ce n'est pas votre reservation");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BusinessException("Reservation deja annulee");
        }
        if (booking.getStatus() == BookingStatus.PAID) {
            throw new BusinessException("Une reservation payee ne peut pas etre annulee ici");
        }
        releaseAndCancel(booking);
        return BookingMapper.toResponse(bookingRepository.save(booking));
    }

    /** Appele par le module paiement apres un paiement reussi. */
    @Transactional
    public void markAsPaid(Booking booking) {
        booking.setStatus(BookingStatus.PAID);
        bookingRepository.save(booking);
    }

    private Booking loadAsOwner(Long ownerId, Long bookingId) {
        Booking booking = getEntity(bookingId);
        if (!booking.getRide().getDriver().getId().equals(ownerId)) {
            throw new AccessDeniedException("Vous n'etes pas le conducteur de ce trajet");
        }
        return booking;
    }

    private void releaseAndCancel(Booking booking) {
        Ride ride = booking.getRide();
        ride.releaseSeats(booking.getSeatsBooked());
        rideRepository.save(ride);
        booking.setStatus(BookingStatus.CANCELLED);
    }
}
