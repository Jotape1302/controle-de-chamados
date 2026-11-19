package com.homeney.ticketsystem.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Configuração do token JWT. Valida o segredo na inicialização para que a aplicação
 * falhe cedo (fail-fast) caso seja iniciada sem um segredo seguro.
 */
@ConfigurationProperties(prefix = "app.security.jwt")
public record JwtProperties(String secret, Duration expiration, String issuer) {

    private static final int MIN_SECRET_BYTES = 32;

    public JwtProperties {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                    "app.security.jwt.secret (JWT_SECRET) deve ter pelo menos " + MIN_SECRET_BYTES + " bytes.");
        }
        if (expiration == null) {
            expiration = Duration.ofHours(8);
        }
        if (issuer == null || issuer.isBlank()) {
            issuer = "homeney-tickets";
        }
    }
}
