package com.cityride.ride;

import com.cityride.ride.dto.CreateRideRequest;
import com.cityride.ride.dto.RideMatchResponse;
import com.cityride.ride.dto.RideResponse;
import com.cityride.ride.dto.UpdateRideRequest;
import com.cityride.ride.matching.SearchCriteria;
import com.cityride.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/rides")
@Tag(name = "Trajets", description = "Creation, recherche (avec matching) et gestion des trajets")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creer un trajet (conducteur)")
    public RideResponse create(@AuthenticationPrincipal CustomUserDetails principal,
                               @Valid @RequestBody CreateRideRequest request) {
        return rideService.create(principal.getId(), request);
    }

    @GetMapping("/mine")
    @Operation(summary = "Mes trajets crees")
    public List<RideResponse> myRides(@AuthenticationPrincipal CustomUserDetails principal) {
        return rideService.getMyRides(principal.getId());
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des trajets, tries par score de compatibilite (matching)")
    public List<RideMatchResponse> search(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false, defaultValue = "1") int minSeats,
            @RequestParam(required = false) Double fromLat,
            @RequestParam(required = false) Double fromLng,
            @RequestParam(required = false) Double toLat,
            @RequestParam(required = false) Double toLng) {

        SearchCriteria criteria = new SearchCriteria(
                from, to, dateTime, maxPrice, minSeats, fromLat, fromLng, toLat, toLng);
        return rideService.search(criteria);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Details d'un trajet")
    public RideResponse getOne(@PathVariable Long id) {
        return rideService.getById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un trajet (conducteur)")
    public RideResponse update(@AuthenticationPrincipal CustomUserDetails principal,
                               @PathVariable Long id,
                               @Valid @RequestBody UpdateRideRequest request) {
        return rideService.update(principal.getId(), id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Annuler un trajet (conducteur)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long id) {
        rideService.cancel(principal.getId(), id);
    }
}
