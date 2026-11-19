package com.homeney.ticketsystem.config;

import com.homeney.ticketsystem.model.User;
import com.homeney.ticketsystem.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Em ambientes sem dados de exemplo (ex.: produção), cria o primeiro administrador
 * a partir de ADMIN_USERNAME / ADMIN_PASSWORD quando ainda não existe nenhum usuário.
 */
@Component
@Profile("!dev")
public class AdminBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);
    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String username;
    private final String password;

    public AdminBootstrap(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          @Value("${app.bootstrap.admin.username:admin}") String username,
                          @Value("${app.bootstrap.admin.password:}") String password) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() > 0) {
            return;
        }
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            log.warn("Nenhum usuário cadastrado. Defina ADMIN_PASSWORD (mín. {} caracteres) para criar o administrador inicial.",
                    MIN_PASSWORD_LENGTH);
            return;
        }
        userRepository.save(new User(username, passwordEncoder.encode(password), "Administrador",
                username + "@homeney.local", true));
        log.info("Administrador inicial '{}' criado.", username);
    }
}
