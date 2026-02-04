package com.foro_hub.repository;

import com.foro_hub.domain.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Fix N+1 problem when loading user profiles
    @EntityGraph(attributePaths = "perfiles")
    Optional<Usuario> findByEmail(String email);

    Boolean existsByEmail(String email);
}
