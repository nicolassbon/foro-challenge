package com.foro_hub.repository;

import com.foro_hub.domain.Curso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CursoRepository extends JpaRepository<Curso, Long> {
    Page<Curso> findByActivoTrue(Pageable pageable);

    Optional<Curso> findByIdAndActivoTrue(Long id);
}
