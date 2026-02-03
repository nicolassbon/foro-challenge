package com.foro_hub.service;

import com.foro_hub.domain.Curso;
import com.foro_hub.dto.curso.CursoCreateDTO;
import com.foro_hub.dto.curso.CursoResponseDTO;
import com.foro_hub.dto.curso.CursoUpdateDTO;
import com.foro_hub.exception.ResourceNotFoundException;
import com.foro_hub.repository.CursoRepository;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para CursoService")
class CursoServiceTest {

    @Mock
    private CursoRepository cursoRepository;

    @InjectMocks
    private CursoService cursoService;

    private CursoCreateDTO cursoCreateDTO;
    private CursoUpdateDTO cursoUpdateDTO;
    private Curso curso;

    @BeforeEach
    void setUp() {
        cursoCreateDTO = new CursoCreateDTO(
                "Spring Boot Avanzado",
                "Backend"
        );

        cursoUpdateDTO = CursoUpdateDTO.builder()
                .withNombre("Spring Boot Avanzado - Actualizado")
                .withCategoria("Backend & Microservicios")
                .build();

        curso = Curso.builder()
                .withId(1L)
                .withNombre("Spring Boot Avanzado")
                .withCategoria("Backend")
                .withActivo(true)
                .build();
    }

    @Test
    @DisplayName("Crear curso con datos válidos debería crear y retornar CursoResponseDTO")
    void crearCurso_conDatosValidos_deberiaCrearYRetornarCursoResponseDTO() {
        // Given
        when(cursoRepository.save(any(Curso.class))).thenReturn(curso);

        // When
        final CursoResponseDTO response = cursoService.crearCurso(cursoCreateDTO);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Spring Boot Avanzado", response.nombre());
        assertEquals("Backend", response.categoria());
    }

    @Test
    @DisplayName("Obtener curso por ID con ID existente debería retornar CursoResponseDTO")
    void obtenerCursoPorId_conIdExistente_deberiaRetornarCursoResponseDTO() {
        // Given
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));

        // When
        final CursoResponseDTO response = cursoService.obtenerCursoPorId(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Spring Boot Avanzado", response.nombre());
        assertEquals("Backend", response.categoria());
    }

    @Test
    @DisplayName("Obtener curso por ID con ID inexistente debería lanzar ResourceNotFoundException")
    void obtenerCursoPorId_conIdInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(cursoRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> cursoService.obtenerCursoPorId(999L)
        );

        assertTrue(exception.getMessage().contains("No se encontró el curso con ID: 999"));
    }

    @Test
    @DisplayName("Listar cursos debería retornar Page de CursoResponseDTO")
    void listarCursos_deberiaRetornarPageDeCursoResponseDTO() {
        // Given
        final List<Curso> cursos = List.of(curso);
        final Page<Curso> pageCursos = new PageImpl<>(cursos);
        final Pageable pageable = PageRequest.of(0, 10);

        when(cursoRepository.findByActivoTrue(pageable)).thenReturn(pageCursos);

        // When
        final Page<CursoResponseDTO> response = cursoService.listarCursos(pageable);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());
        assertEquals("Spring Boot Avanzado", response.getContent().get(0).nombre());
    }

    @Test
    @DisplayName("Actualizar curso con datos válidos debería actualizar y retornar CursoResponseDTO")
    void actualizarCurso_conDatosValidos_deberiaActualizarYRetornarCursoResponseDTO() {
        // Given
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(cursoRepository.save(any(Curso.class))).thenReturn(curso);

        // When
        final CursoResponseDTO response = cursoService.actualizarCurso(1L, cursoUpdateDTO);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.id());
    }

    @Test
    @DisplayName("Actualizar curso con ID inexistente debería lanzar ResourceNotFoundException")
    void actualizarCurso_conIdInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(cursoRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> cursoService.actualizarCurso(999L, cursoUpdateDTO)
        );

        assertTrue(exception.getMessage().contains("No se encontró el curso con ID: 999"));
    }

    @Test
    @DisplayName("Eliminar curso con ID existente debería marcar como inactivo")
    void eliminarCurso_conIdExistente_deberiaMarcarComoInactivo() {
        // Given
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(cursoRepository.save(any(Curso.class))).thenReturn(curso);

        // When
        cursoService.eliminarCurso(1L);

        // Then
        assertFalse(curso.getActivo());
    }

    @Test
    @DisplayName("Eliminar curso con ID inexistente debería lanzar ResourceNotFoundException")
    void eliminarCurso_conIdInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(cursoRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> cursoService.eliminarCurso(999L)
        );

        assertTrue(exception.getMessage().contains("No se encontró el curso con ID: 999"));
    }

    @Test
    @DisplayName("Crear curso debería guardar en el repositorio")
    void crearCurso_deberiaGuardarEnRepositorio() {
        // Given
        when(cursoRepository.save(any(Curso.class))).thenReturn(curso);

        // When
        cursoService.crearCurso(cursoCreateDTO);

        // Then
        verify(cursoRepository, times(1)).save(any(Curso.class));
    }

    @Test
    @DisplayName("Actualizar curso debería guardar los cambios en el repositorio")
    void actualizarCurso_deberiaGuardarCambiosEnRepositorio() {
        // Given
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(cursoRepository.save(any(Curso.class))).thenReturn(curso);

        // When
        cursoService.actualizarCurso(1L, cursoUpdateDTO);

        // Then
        verify(cursoRepository, times(1)).save(any(Curso.class));
    }

    @Test
    @DisplayName("Eliminar curso debería guardar el estado inactivo en el repositorio")
    void eliminarCurso_deberiaGuardarEstadoInactivoEnRepositorio() {
        // Given
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(cursoRepository.save(any(Curso.class))).thenReturn(curso);

        // When
        cursoService.eliminarCurso(1L);

        // Then
        verify(cursoRepository, times(1)).save(any(Curso.class));
        assertFalse(curso.getActivo());
    }
}
