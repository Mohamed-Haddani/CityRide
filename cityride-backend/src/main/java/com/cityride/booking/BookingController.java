package com.cityride.booking;

import com.cityride.booking.dto.BookingResponse;
import com.cityride.booking.dto.CreateBookingRequest;
import com.cityride.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Reservations", description = "Reservation de places et gestion par le conducteur")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Reserver une ou plusieurs places (passager)")
    public BookingResponse create(@AuthenticationPrincipal CustomUserDetails principal,
                                  @Valid @RequestBody CreateBookingRequest request) {
        return bookingService.create(principal.getId(), request);
    }

    @GetMapping("/bookings/mine")
    @Operation(summary = "Mes reservations (passager)")
    public List<BookingResponse> myBookings(@AuthenticationPrincipal CustomUserDetails principal) {
        return bookingService.getMyBookings(principal.getId());
    }

    @GetMapping("/bookings/{id}")
    @Operation(summary = "Detail d'une reservation")
    public BookingResponse getOne(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long id) {
        return bookingService.getOne(principal.getId(), id);
    }

    @GetMapping("/rides/{rideId}/bookings")
    @Operation(summary = "Reservations recues sur un trajet (conducteur)")
    public List<BookingResponse> rideBookings(@AuthenticationPrincipal CustomUserDetails principal,
                                              @PathVariable Long rideId) {
        return bookingService.getRideBookings(principal.getId(), rideId);
    }

    @PatchMapping("/bookings/{id}/accept")
    @Operation(summary = "Accepter une reservation (conducteur)")
    public BookingResponse accept(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long id) {
        return bookingService.accept(principal.getId(), id);
    }

    @PatchMapping("/bookings/{id}/reject")
    @Operation(summary = "Refuser une reservation (conducteur)")
    public BookingResponse reject(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long id) {
        return bookingService.reject(principal.getId(), id);
    }

    @PatchMapping("/bookings/{id}/cancel")
    @Operation(summary = "Annuler ma reservation (passager)")
    public BookingResponse cancel(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long id) {
        return bookingService.cancel(principal.getId(), id);
    }
}
