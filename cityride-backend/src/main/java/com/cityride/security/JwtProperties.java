package com.cityride.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Parametres JWT lus depuis application.yml (prefixe app.jwt).
 */
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String secret,
        long accessTokenExpiration,
        long refreshTokenExpiration
) {
}
