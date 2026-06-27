package com.cityride.security;

import com.cityride.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * Generation et validation des JSON Web Tokens (HS256).
 * Deux types de tokens : "access" (court) et "refresh" (long).
 */
@Service
public class JwtService {

    private static final String CLAIM_TYPE = "type";
    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_USER_ID = "uid";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    private final SecretKey key;
    private final long accessExpiration;
    private final long refreshExpiration;

    public JwtService(JwtProperties properties) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secret()));
        this.accessExpiration = properties.accessTokenExpiration();
        this.refreshExpiration = properties.refreshTokenExpiration();
    }

    public String generateAccessToken(User user) {
        return buildToken(user, TYPE_ACCESS, accessExpiration);
    }

    public String generateRefreshToken(User user) {
        return buildToken(user, TYPE_REFRESH, refreshExpiration);
    }

    public long getAccessExpirationMs() {
        return accessExpiration;
    }

    private String buildToken(User user, String type, long expirationMs) {
        Date now = new Date();
        return Jwts.builder()
                .subject(user.getEmail())
                .claims(Map.of(
                        CLAIM_USER_ID, user.getId(),
                        CLAIM_ROLE, user.getRole().name(),
                        CLAIM_TYPE, type))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return parse(token).getSubject();
    }

    public boolean isAccessToken(String token) {
        return TYPE_ACCESS.equals(parse(token).get(CLAIM_TYPE, String.class));
    }

    public boolean isRefreshToken(String token) {
        return TYPE_REFRESH.equals(parse(token).get(CLAIM_TYPE, String.class));
    }

    /**
     * Verifie la signature et l'expiration. Retourne false si le token est invalide.
     */
    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
