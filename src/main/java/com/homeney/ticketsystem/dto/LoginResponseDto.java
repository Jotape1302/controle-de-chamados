package com.homeney.ticketsystem.dto;

import java.time.Instant;

/**
 * Resposta de login bem-sucedido: token Bearer e dados públicos do usuário.
 */
public record LoginResponseDto(String accessToken, String tokenType, Instant expiresAt, UserDto user) {

    public static LoginResponseDto bearer(String accessToken, Instant expiresAt, UserDto user) {
        return new LoginResponseDto(accessToken, "Bearer", expiresAt, user);
    }
}
