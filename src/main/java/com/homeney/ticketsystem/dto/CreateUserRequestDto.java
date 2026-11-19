package com.homeney.ticketsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Dados para cadastro de usuário. Usar um DTO (e não a entidade) evita mass assignment
 * de campos como id ou createdAt.
 */
public record CreateUserRequestDto(
        @NotBlank(message = "O nome de usuário é obrigatório.")
        @Size(min = 3, max = 50, message = "O nome de usuário deve ter entre 3 e 50 caracteres.")
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Use apenas letras, números, ponto, hífen ou underline.")
        String username,

        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres.")
        String password,

        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
        String fullName,

        @Email(message = "E-mail inválido.")
        @Size(max = 100, message = "O e-mail deve ter no máximo 100 caracteres.")
        String email,

        boolean admin
) {
}
