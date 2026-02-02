package com.foro_hub.repository;

import com.foro_hub.domain.Topico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicoRepository extends JpaRepository<Topico, Long> {

    boolean existsByTituloAndMensaje(String titulo, String mensaje);
}
