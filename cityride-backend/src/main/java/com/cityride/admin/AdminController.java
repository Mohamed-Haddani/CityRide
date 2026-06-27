package com.cityride.admin;

import com.cityride.booking.dto.BookingResponse;
import com.cityride.ride.dto.RideResponse;
import com.cityride.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Administration", description = "Reserve aux administrateurs")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    @Operation(summary = "Lister tous les utilisateurs")
    public List<UserResponse> users() {
        return adminService.listUsers();
    }

    @PatchMapping("/users/{id}/block")
    @Operation(summary = "Bloquer un utilisateur")
    public UserResponse block(@PathVariable Long id) {
        return adminService.setBlocked(id, true);
    }

    @PatchMapping("/users/{id}/unblock")
    @Operation(summary = "Debloquer un utilisateur")
    public UserResponse unblock(@PathVariable Long id) {
        return adminService.setBlocked(id, false);
    }

    @GetMapping("/rides")
    @Operation(summary = "Lister tous les trajets")
    public List<RideResponse> rides() {
        return adminService.listRides();
    }

    @GetMapping("/bookings")
    @Operation(summary = "Lister toutes les reservations")
    public List<BookingResponse> bookings() {
        return adminService.listBookings();
    }
}
