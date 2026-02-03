package com.foro_hub.repository;

import com.foro_hub.domain.Curso;
import com.foro_hub.domain.Topico;
import com.foro_hub.domain.Usuario;
import com.foro_hub.domain.enums.StatusTopico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TopicoRepositoryTest {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private TestEntityManager em;

    private Usuario usuario;
    private Curso curso;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .withId(null)
                .withNombre("Juan")
                .withEmail("juan@test.com")
                .withContrasena("123456")
                .withActivo(true)
                .build();
        em.persist(usuario);

        curso = Curso.builder()
                .withNombre("Spring Boot")
                .withCategoria("Backend")
                .withActivo(true)
                .build();
        em.persist(curso);
    }

    @Test
    @DisplayName("Debería retornar true si existe un tópico con el mismo título y mensaje")
    void existsByTituloAndMensaje_Scenario1() {
        // GIVEN
        registrarTopico("Duda Java", "Mensaje duplicado", true);

        // WHEN
        boolean existe = topicoRepository.existsByTituloAndMensaje("Duda Java", "Mensaje duplicado");

        // THEN
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Debería retornar false si NO existe coincidencia exacta")
    void existsByTituloAndMensaje_Scenario2() {
        // GIVEN
        registrarTopico("Duda Java", "Mensaje original", true);

        // WHEN
        boolean existe = topicoRepository.existsByTituloAndMensaje("Duda Python", "Otro mensaje");

        // THEN
        assertThat(existe).isFalse();
    }

    @Test
    @DisplayName("Debería retornar solo tópicos activos")
    void findByActivoTrue() {
        // GIVEN
        registrarTopico("Topico Activo 1", "msg1", true);
        registrarTopico("Topico Activo 2", "msg2", true);
        registrarTopico("Topico Eliminado", "msg3", false);

        // WHEN
        Page<Topico> resultado = topicoRepository.findByActivoTrue(PageRequest.of(0, 10));

        // THEN
        assertThat(resultado.getTotalElements()).isEqualTo(2);
        assertThat(resultado.getContent()).extracting(Topico::getTitulo)
                .contains("Topico Activo 1", "Topico Activo 2")
                .doesNotContain("Topico Eliminado");
    }

    private void registrarTopico(String titulo, String mensaje, boolean activo) {
        Topico topico = Topico.builder()
                .withTitulo(titulo)
                .withMensaje(mensaje)
                .withFechaCreacion(LocalDateTime.now())
                .withStatus(StatusTopico.ABIERTO)
                .withAutor(usuario)
                .withCurso(curso)
                .withActivo(activo)
                .build();
        em.persist(topico);
    }
}
