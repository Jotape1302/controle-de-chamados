package com.homeney.ticketsystem.controller;

import com.homeney.ticketsystem.dto.LoginRequestDto;
import com.homeney.ticketsystem.dto.LoginResponseDto;
import com.homeney.ticketsystem.dto.UserDto;
import com.homeney.ticketsystem.model.User;
import com.homeney.ticketsystem.security.TokenService;
import com.homeney.ticketsystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Login e dados do usuário autenticado")
public class AuthController {

    private final UserService userService;
    private final TokenService tokenService;

    public AuthController(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    @SecurityRequirements
    @Operation(summary = "Login", description = "Valida usuário e senha e retorna um token JWT (Bearer)")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        User user = userService.authenticate(request);
        TokenService.IssuedToken token = tokenService.issue(user);
        return ResponseEntity.ok(LoginResponseDto.bearer(token.value(), token.expiresAt(), UserDto.fromEntity(user)));
    }

    @GetMapping("/me")
    @Operation(summary = "Usuário autenticado", description = "Retorna os dados do usuário dono do token")
    public ResponseEntity<UserDto> me(Authentication authentication) {
        return ResponseEntity.ok(userService.getUserByUsername(authentication.getName()));
    }
}
