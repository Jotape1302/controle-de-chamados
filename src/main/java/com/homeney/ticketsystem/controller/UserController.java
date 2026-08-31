package com.homeney.ticketsystem.controller;

import com.homeney.ticketsystem.dto.LoginRequestDto;
import com.homeney.ticketsystem.dto.LoginResponseDto;
import com.homeney.ticketsystem.dto.UserDto;
import com.homeney.ticketsystem.model.User;
import com.homeney.ticketsystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Usuários e Autenticação", description = "Endpoints para autenticação e gestão de operadores e administradores")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/auth/login")
    @Operation(summary = "Login de usuário", description = "Autentica usuário e senha retornando credenciais e perfil de acesso")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        LoginResponseDto response = userService.authenticate(request);
        if (!response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    @Operation(summary = "Listar usuários", description = "Retorna a lista de todos os usuários cadastrados")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{username}")
    @Operation(summary = "Buscar usuário por nome", description = "Retorna os detalhes de um usuário específico")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PostMapping("/users")
    @Operation(summary = "Cadastrar usuário", description = "Registra um novo usuário ou administrador")
    public ResponseEntity<UserDto> createUser(@RequestBody User user) {
        UserDto created = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
