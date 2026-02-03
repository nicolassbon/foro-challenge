package com.foro_hub.repository;

import com.foro_hub.domain.Curso;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CursoRepositoryTest {

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("Debería retornar solo cursos activos paginados")
    void findByActivoTrue_retornaSoloActivos() {
        // GIVEN
        registrarCurso("Java Basics", "Backend", true);
        registrarCurso("Python Data Science", "Data", true);
        registrarCurso("Curso Obsoleto", "Legacy", false);

        Pageable pageable = PageRequest.of(0, 10);

        // WHEN
        Page<Curso> resultado = cursoRepository.findByActivoTrue(pageable);

        // THEN
        assertThat(resultado.getTotalElements()).isEqualTo(2);
        assertThat(resultado.getContent()).extracting(Curso::getNombre)
                .contains("Java Basics", "Python Data Science")
                .doesNotContain("Curso Obsoleto");
    }

    private void registrarCurso(String nombre, String categoria, boolean activo) {
        Curso curso = Curso.builder()
                .withNombre(nombre)
                .withCategoria(categoria)
                .withActivo(activo)
                .build();
        em.persist(curso);
    }
}
