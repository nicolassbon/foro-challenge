package com.foro_hub.service;

import com.foro_hub.domain.Perfil;
import com.foro_hub.domain.Usuario;
import com.foro_hub.dto.auth.AuthResponseDTO;
import com.foro_hub.dto.auth.LoginRequestDTO;
import com.foro_hub.dto.auth.RegisterRequestDTO;
import com.foro_hub.dto.auth.RegisterResponseDTO;
import com.foro_hub.exception.EmailAlreadyExistsException;
import com.foro_hub.exception.ResourceNotFoundException;
import com.foro_hub.repository.PerfilRepository;
import com.foro_hub.repository.UsuarioRepository;
import com.foro_hub.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public RegisterResponseDTO register(final RegisterRequestDTO request) {
        log.info("[ForoHub/Auth] - Registrando nuevo usuario con email: {}", request.email());

        final boolean emailExists = usuarioRepository.existsByEmail(request.email());
        if (emailExists) {
            log.error("[ForoHub/Auth] - Email ya registrado: {}", request.email());
            throw new EmailAlreadyExistsException("Ya existe una cuenta con el email proporcionado.");
        }

        final Perfil perfilUser = perfilRepository.findByNombre("USER")
                .orElseThrow(() -> new ResourceNotFoundException("Perfil USER no encontrado en la base de datos"));

        final Set<Perfil> perfiles = new HashSet<>();
        perfiles.add(perfilUser);

        final Usuario usuario = Usuario.builder()
                .withNombre(request.nombre())
                .withEmail(request.email())
                .withContrasena(passwordEncoder.encode(request.contrasena()))
                .withPerfiles(perfiles)
                .withActivo(true)
                .build();

        final Usuario usuarioGuardado = usuarioRepository.save(usuario);
        log.info("[ForoHub/Auth] - Usuario registrado exitosamente con ID: {}", usuarioGuardado.getId());

        return RegisterResponseDTO.builder()
                .withId(usuarioGuardado.getId())
                .withNombre(usuarioGuardado.getNombre())
                .withEmail(usuarioGuardado.getEmail())
                .build();
    }

    public AuthResponseDTO login(final LoginRequestDTO request) {
        log.info("[ForoHub/Auth] - Intento de login para email: {}", request.email());

        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.contrasena())
        );

        final Usuario usuario = (Usuario) authentication.getPrincipal();
        log.info("[ForoHub/Auth] - Login exitoso para usuario: {}", usuario.getEmail());

        final String token = jwtService.generateToken(usuario);

        return AuthResponseDTO.builder()
                .withToken(token)
                .withExpiresIn(jwtService.getExpirationMs())
                .build();
    }
}
