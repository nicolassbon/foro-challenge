package com.foro_hub.repository;

import com.foro_hub.domain.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {

    Optional<Perfil> findByNombre(String nombre);
}
