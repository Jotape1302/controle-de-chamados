package com.homeney.ticketsystem.security;

import com.homeney.ticketsystem.model.User;
import org.springframework.security.core.Authentication;

/**
 * Perfis de acesso. São gravados no token como "roles" e expostos ao Spring Security como ROLE_*.
 */
public enum Role {
    ADMIN,
    USER;

    public static Role of(User user) {
        return user.isAdmin() ? ADMIN : USER;
    }

    public static boolean isAdmin(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> ("ROLE_" + ADMIN.name()).equals(a.getAuthority()));
    }
}
