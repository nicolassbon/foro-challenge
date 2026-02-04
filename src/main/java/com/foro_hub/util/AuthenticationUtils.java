package com.foro_hub.util;

import com.foro_hub.domain.Usuario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class AuthenticationUtils {

    public static Usuario getAuthenticatedUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("[ForoHub/Security] - No se encontró usuario autenticado en el contexto de seguridad");
            throw new IllegalStateException("Usuario no autenticado");
        }

        final Object principal = authentication.getPrincipal();

        if (!(principal instanceof Usuario usuario)) {
            log.error("[ForoHub/Security] - El principal no es una instancia de Usuario: {}", principal.getClass().getName());
            throw new IllegalStateException("Usuario no autenticado correctamente");
        }

        log.debug("[ForoHub/Security] - Usuario autenticado obtenido: {}", usuario.getEmail());

        return usuario;
    }
}
