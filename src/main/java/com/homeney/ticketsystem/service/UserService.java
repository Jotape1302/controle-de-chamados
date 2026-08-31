package com.homeney.ticketsystem.service;

import com.homeney.ticketsystem.dto.LoginRequestDto;
import com.homeney.ticketsystem.dto.LoginResponseDto;
import com.homeney.ticketsystem.dto.UserDto;
import com.homeney.ticketsystem.exception.BusinessException;
import com.homeney.ticketsystem.exception.ResourceNotFoundException;
import com.homeney.ticketsystem.model.User;
import com.homeney.ticketsystem.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário '" + username + "' não encontrado."));
        return UserDto.fromEntity(user);
    }

    @Transactional
    public UserDto createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BusinessException("Já existe um usuário cadastrado com o nome de usuário: " + user.getUsername());
        }
        if (user.getFullName() == null || user.getFullName().isBlank()) {
            user.setFullName(user.getUsername());
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            user.setEmail(user.getUsername().toLowerCase() + "@homeney.local");
        }
        User saved = userRepository.save(user);
        return UserDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public LoginResponseDto authenticate(LoginRequestDto request) {
        Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());
        if (optionalUser.isEmpty() || !optionalUser.get().getPassword().equals(request.getPassword())) {
            return new LoginResponseDto(false, "Usuário ou senha inválidos!", null);
        }

        User user = optionalUser.get();
        return new LoginResponseDto(true, "Login realizado com sucesso!", UserDto.fromEntity(user));
    }
}
