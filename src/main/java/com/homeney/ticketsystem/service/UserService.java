package com.homeney.ticketsystem.service;

import com.homeney.ticketsystem.dto.CreateUserRequestDto;
import com.homeney.ticketsystem.dto.LoginRequestDto;
import com.homeney.ticketsystem.dto.UserDto;
import com.homeney.ticketsystem.exception.BusinessException;
import com.homeney.ticketsystem.exception.ResourceNotFoundException;
import com.homeney.ticketsystem.model.User;
import com.homeney.ticketsystem.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    /** Hash usado quando o usuário não existe, para que o tempo de resposta não revele usuários válidos. */
    private final String dummyHash;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.dummyHash = passwordEncoder.encode("dummy-password-for-timing");
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserDto getUserByUsername(String username) {
        return UserDto.fromEntity(findEntity(username));
    }

    @Transactional
    public UserDto createUser(CreateUserRequestDto request) {
        String username = request.username().trim();
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException("Já existe um usuário cadastrado com o nome de usuário: " + username);
        }

        String fullName = isBlank(request.fullName()) ? username : request.fullName().trim();
        String email = isBlank(request.email())
                ? username.toLowerCase(Locale.ROOT) + "@homeney.local"
                : request.email().trim();

        User user = new User(username, passwordEncoder.encode(request.password()), fullName, email, request.admin());
        return UserDto.fromEntity(userRepository.save(user));
    }

    /**
     * Valida as credenciais e retorna a entidade do usuário.
     *
     * @throws BadCredentialsException se usuário ou senha forem inválidos
     */
    @Transactional(readOnly = true)
    public User authenticate(LoginRequestDto request) {
        User user = userRepository.findByUsername(request.getUsername().trim()).orElse(null);
        String hash = user != null ? user.getPassword() : dummyHash;
        boolean matches = passwordEncoder.matches(request.getPassword(), hash);
        if (user == null || !matches) {
            throw new BadCredentialsException("Usuário ou senha inválidos.");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public User findEntity(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário '" + username + "' não encontrado."));
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
