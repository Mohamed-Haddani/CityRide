package com.cityride.auth;

import com.cityride.auth.dto.AuthResponse;
import com.cityride.auth.dto.LoginRequest;
import com.cityride.auth.dto.RefreshRequest;
import com.cityride.auth.dto.RegisterRequest;
import com.cityride.security.CustomUserDetails;
import com.cityride.user.UserService;
import com.cityride.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "Inscription, connexion, rafraichissement de token")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creer un compte")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Se connecter")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Obtenir un nouvel access token a partir du refresh token")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request);
    }

    @GetMapping("/me")
    @Operation(summary = "Recuperer mon profil (utilisateur connecte)")
    public UserResponse me(@AuthenticationPrincipal CustomUserDetails principal) {
        return userService.getProfile(principal.getId());
    }
}
