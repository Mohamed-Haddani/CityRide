package com.cityride.user;

import com.cityride.security.CustomUserDetails;
import com.cityride.user.dto.UpdateProfileRequest;
import com.cityride.user.dto.UserResponse;
import com.cityride.user.dto.UserSummary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Utilisateurs", description = "Profils publics et gestion de son propre profil")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Profil public d'un utilisateur")
    public UserSummary getPublicProfile(@PathVariable Long id) {
        return userService.getPublicProfile(id);
    }

    @PutMapping("/me")
    @Operation(summary = "Modifier mon profil")
    public UserResponse updateMyProfile(@AuthenticationPrincipal CustomUserDetails principal,
                                        @Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateProfile(principal.getId(), request);
    }
}
