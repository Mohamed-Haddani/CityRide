package com.cityride.auth;

import com.cityride.auth.dto.AuthResponse;
import com.cityride.auth.dto.LoginRequest;
import com.cityride.auth.dto.RefreshRequest;
import com.cityride.auth.dto.RegisterRequest;
import com.cityride.common.exception.ConflictException;
import com.cityride.security.JwtService;
import com.cityride.user.Role;
import com.cityride.user.User;
import com.cityride.user.UserMapper;
import com.cityride.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Cet email est deja utilise");
        }
        User user = new User();
        user.setEmail(request.email().toLowerCase().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhone(request.phone());
        user.setCity(request.city());
        user.setRole(Role.USER);
        user = userRepository.save(user);
        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // Delegue la verification du mot de passe (et du blocage) a Spring Security.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Identifiants invalides"));
        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse refresh(RefreshRequest request) {
        String token = request.refreshToken();
        if (!jwtService.isValid(token) || !jwtService.isRefreshToken(token)) {
            throw new BadCredentialsException("Refresh token invalide ou expire");
        }
        User user = userRepository.findByEmail(jwtService.extractUsername(token))
                .orElseThrow(() -> new BadCredentialsException("Refresh token invalide"));
        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        return new AuthResponse(
                jwtService.generateAccessToken(user),
                jwtService.generateRefreshToken(user),
                "Bearer",
                jwtService.getAccessExpirationMs() / 1000,
                UserMapper.toResponse(user));
    }
}
