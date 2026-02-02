package com.foro_hub.service;

import com.foro_hub.domain.Curso;
import com.foro_hub.domain.Topico;
import com.foro_hub.domain.Usuario;
import com.foro_hub.domain.enums.StatusTopico;
import com.foro_hub.dto.topico.TopicoCreateDTO;
import com.foro_hub.dto.topico.TopicoResponseDTO;
import com.foro_hub.dto.topico.TopicoUpdateDTO;
import com.foro_hub.exception.DuplicateTopicoException;
import com.foro_hub.exception.ResourceNotFoundException;
import com.foro_hub.repository.CursoRepository;
import com.foro_hub.repository.TopicoRepository;
import com.foro_hub.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para TopicoService")
class TopicoServiceTest {

    @Mock
    private TopicoRepository topicoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CursoRepository cursoRepository;

    @InjectMocks
    private TopicoService topicoService;

    private TopicoCreateDTO topicoCreateDTO;
    private TopicoUpdateDTO topicoUpdateDTO;
    private Usuario usuario;
    private Curso curso;
    private Topico topico;

    @BeforeEach
    void setUp() {
        // Given - preparar datos de prueba
        topicoCreateDTO = TopicoCreateDTO.builder()
                .withTitulo("¿Cómo aprender Spring Boot?")
                .withMensaje("Necesito recursos para aprender Spring Boot desde cero")
                .withIdAutor(1L)
                .withIdCurso(1L)
                .build();

        topicoUpdateDTO = TopicoUpdateDTO.builder()
                .withTitulo("¿Cómo aprender Spring Boot? - Actualizado")
                .withMensaje("Necesito recursos actualizados para aprender Spring Boot")
                .withStatus(StatusTopico.CERRADO)
                .build();

        usuario = Usuario.builder()
                .withId(1L)
                .withNombre("Juan Pérez")
                .withEmail("juan@test.com")
                .withContrasena("password123")
                .withActivo(true)
                .build();

        curso = Curso.builder()
                .withId(1L)
                .withNombre("Spring Boot")
                .withCategoria("Backend")
                .withActivo(true)
                .build();

        topico = Topico.builder()
                .withId(1L)
                .withTitulo("¿Cómo aprender Spring Boot?")
                .withMensaje("Necesito recursos para aprender Spring Boot desde cero")
                .withFechaCreacion(LocalDateTime.now())
                .withStatus(StatusTopico.ABIERTO)
                .withAutor(usuario)
                .withCurso(curso)
                .withRespuestas(new ArrayList<>())
                .withActivo(true)
                .build();
    }

    @Test
    @DisplayName("Crear tópico con datos válidos debería crear y retornar TopicoResponseDTO")
    void crearTopico_conDatosValidos_deberiaCrearYRetornarTopicoResponseDTO() {
        // Given
        when(topicoRepository.existsByTituloAndMensaje(anyString(), anyString())).thenReturn(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(topicoRepository.save(any(Topico.class))).thenReturn(topico);

        // When
        final TopicoResponseDTO response = topicoService.crearTopico(topicoCreateDTO);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("¿Cómo aprender Spring Boot?", response.titulo());
        assertEquals("Necesito recursos para aprender Spring Boot desde cero", response.mensaje());
        assertNotNull(response.fechaCreacion());
    }

    @Test
    @DisplayName("Crear tópico con tópico duplicado debería lanzar DuplicateTopicoException")
    void crearTopico_conTopicoDuplicado_deberiaLanzarDuplicateTopicoException() {
        // Given
        when(topicoRepository.existsByTituloAndMensaje(anyString(), anyString())).thenReturn(true);

        // When & Then
        final DuplicateTopicoException exception = assertThrows(
                DuplicateTopicoException.class,
                () -> topicoService.crearTopico(topicoCreateDTO)
        );

        assertEquals("Ya existe un tópico con el mismo título y mensaje.", exception.getMessage());
    }

    @Test
    @DisplayName("Crear tópico con autor inexistente debería lanzar ResourceNotFoundException")
    void crearTopico_conAutorInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(topicoRepository.existsByTituloAndMensaje(anyString(), anyString())).thenReturn(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> topicoService.crearTopico(topicoCreateDTO)
        );

        assertTrue(exception.getMessage().contains("No se encontró el usuario con ID: 1"));
    }

    @Test
    @DisplayName("Crear tópico con curso inexistente debería lanzar ResourceNotFoundException")
    void crearTopico_conCursoInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(topicoRepository.existsByTituloAndMensaje(anyString(), anyString())).thenReturn(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(cursoRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> topicoService.crearTopico(topicoCreateDTO)
        );

        assertTrue(exception.getMessage().contains("No se encontró el curso con ID: 1"));
    }

    @Test
    @DisplayName("Obtener tópico por ID con ID existente debería retornar TopicoResponseDTO")
    void obtenerTopicoPorId_conIdExistente_deberiaRetornarTopicoResponseDTO() {
        // Given
        when(topicoRepository.findById(1L)).thenReturn(Optional.of(topico));

        // When
        final TopicoResponseDTO response = topicoService.obtenerTopicoPorId(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("¿Cómo aprender Spring Boot?", response.titulo());
        assertEquals("Necesito recursos para aprender Spring Boot desde cero", response.mensaje());
        assertNotNull(response.fechaCreacion());
    }

    @Test
    @DisplayName("Obtener tópico por ID con ID inexistente debería lanzar ResourceNotFoundException")
    void obtenerTopicoPorId_conIdInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(topicoRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> topicoService.obtenerTopicoPorId(999L)
        );

        assertTrue(exception.getMessage().contains("No se encontró el tópico con ID: 999"));
    }

    @Test
    @DisplayName("Listar tópicos debería retornar Page de TopicoResponseDTO")
    void listarTopicos_deberiaRetornarPageDeTopicoResponseDTO() {
        // Given
        final List<Topico> topicos = List.of(topico);
        final Page<Topico> pageTopicos = new PageImpl<>(topicos);
        final Pageable pageable = PageRequest.of(0, 10);

        when(topicoRepository.findAll(pageable)).thenReturn(pageTopicos);

        // When
        final Page<TopicoResponseDTO> response = topicoService.listarTopicos(pageable);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());
        assertEquals("¿Cómo aprender Spring Boot?", response.getContent().get(0).titulo());
    }

    @Test
    @DisplayName("Actualizar tópico con datos válidos debería actualizar y retornar TopicoResponseDTO")
    void actualizarTopico_conDatosValidos_deberiaActualizarYRetornarTopicoResponseDTO() {
        // Given
        when(topicoRepository.findById(1L)).thenReturn(Optional.of(topico));
        when(topicoRepository.save(any(Topico.class))).thenReturn(topico);

        // When
        final TopicoResponseDTO response = topicoService.actualizarTopico(1L, topicoUpdateDTO);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.id());
    }

    @Test
    @DisplayName("Actualizar tópico con ID inexistente debería lanzar ResourceNotFoundException")
    void actualizarTopico_conIdInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(topicoRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> topicoService.actualizarTopico(999L, topicoUpdateDTO)
        );

        assertTrue(exception.getMessage().contains("No se encontró el tópico con ID: 999"));
    }

    @Test
    @DisplayName("Eliminar tópico con ID existente debería marcar como inactivo")
    void eliminarTopico_conIdExistente_deberiaMarcarComoInactivo() {
        // Given
        when(topicoRepository.findById(1L)).thenReturn(Optional.of(topico));
        when(topicoRepository.save(any(Topico.class))).thenReturn(topico);

        // When
        topicoService.eliminarTopico(1L);

        // Then
        assertFalse(topico.getActivo());
    }

    @Test
    @DisplayName("Eliminar tópico con ID inexistente debería lanzar ResourceNotFoundException")
    void eliminarTopico_conIdInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(topicoRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> topicoService.eliminarTopico(999L)
        );

        assertTrue(exception.getMessage().contains("No se encontró el tópico con ID: 999"));
    }
}
