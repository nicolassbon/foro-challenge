package com.foro_hub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foro_hub.domain.enums.StatusTopico;
import com.foro_hub.dto.topico.TopicoCreateDTO;
import com.foro_hub.dto.topico.TopicoResponseDTO;
import com.foro_hub.dto.topico.TopicoUpdateDTO;
import com.foro_hub.exception.DuplicateTopicoException;
import com.foro_hub.exception.GlobalExceptionHandler;
import com.foro_hub.exception.ResourceNotFoundException;
import com.foro_hub.service.TopicoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TopicoController Unit Tests")
class TopicoControllerTest {

    private final String API_TOPICOS = "/topicos";
    private MockMvc mockMvc;

    @Mock
    private TopicoService topicoService;

    private ObjectMapper objectMapper;
    private TopicoCreateDTO validTopicoCreateDTO;
    private TopicoUpdateDTO validTopicoUpdateDTO;
    private TopicoResponseDTO successTopicoResponse;

    @BeforeEach
    void setUp() {
        TopicoController topicoController = new TopicoController(topicoService);
        mockMvc = MockMvcBuilders.standaloneSetup(topicoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        validTopicoCreateDTO = TopicoCreateDTO.builder()
                .withTitulo("¿Cómo aprender Spring Boot?")
                .withMensaje("Necesito recursos para aprender Spring Boot desde cero")
                .withIdAutor(1L)
                .withIdCurso(1L)
                .build();

        validTopicoUpdateDTO = TopicoUpdateDTO.builder()
                .withTitulo("¿Cómo aprender Spring Boot? - Actualizado")
                .withMensaje("Necesito recursos actualizados para aprender Spring Boot")
                .withStatus(StatusTopico.CERRADO)
                .build();

        successTopicoResponse = TopicoResponseDTO.builder()
                .withId(1L)
                .withTitulo("¿Cómo aprender Spring Boot?")
                .withMensaje("Necesito recursos para aprender Spring Boot desde cero")
                .withFechaCreacion(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should create topico successfully and return 201 CREATED")
    void crearTopicoValidRequestReturnsCreated() throws Exception {
        when(topicoService.crearTopico(any(TopicoCreateDTO.class))).thenReturn(successTopicoResponse);

        mockMvc.perform(post(API_TOPICOS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTopicoCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.titulo").value("¿Cómo aprender Spring Boot?"))
                .andExpect(jsonPath("$.mensaje").value("Necesito recursos para aprender Spring Boot desde cero"))
                .andExpect(jsonPath("$.fechaCreacion").exists());

        verify(topicoService).crearTopico(any(TopicoCreateDTO.class));
    }

    @Test
    @DisplayName("Should return 400 for empty titulo")
    void crearTopicoEmptyTituloReturnsBadRequest() throws Exception {
        TopicoCreateDTO invalidDTO = TopicoCreateDTO.builder()
                .withTitulo("")
                .withMensaje("Mensaje válido")
                .withIdAutor(1L)
                .withIdCurso(1L)
                .build();

        mockMvc.perform(post(API_TOPICOS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(topicoService, never()).crearTopico(any());
    }

    @Test
    @DisplayName("Should return 400 for empty mensaje")
    void crearTopicoEmptyMensajeReturnsBadRequest() throws Exception {
        TopicoCreateDTO invalidDTO = TopicoCreateDTO.builder()
                .withTitulo("Título válido")
                .withMensaje("")
                .withIdAutor(1L)
                .withIdCurso(1L)
                .build();

        mockMvc.perform(post(API_TOPICOS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(topicoService, never()).crearTopico(any());
    }

    @Test
    @DisplayName("Should return 400 for null idAutor")
    void crearTopicoNullAutorIdReturnsBadRequest() throws Exception {
        TopicoCreateDTO invalidDTO = TopicoCreateDTO.builder()
                .withTitulo("Título válido")
                .withMensaje("Mensaje válido")
                .withIdAutor(null)
                .withIdCurso(1L)
                .build();

        mockMvc.perform(post(API_TOPICOS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(topicoService, never()).crearTopico(any());
    }

    @Test
    @DisplayName("Should return 400 for null idCurso")
    void crearTopicoNullCursoIdReturnsBadRequest() throws Exception {
        TopicoCreateDTO invalidDTO = TopicoCreateDTO.builder()
                .withTitulo("Título válido")
                .withMensaje("Mensaje válido")
                .withIdAutor(1L)
                .withIdCurso(null)
                .build();

        mockMvc.perform(post(API_TOPICOS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(topicoService, never()).crearTopico(any());
    }

    @Test
    @DisplayName("Should return 409 for duplicate topico")
    void crearTopicoDuplicateReturnsConflict() throws Exception {
        when(topicoService.crearTopico(any(TopicoCreateDTO.class)))
                .thenThrow(new DuplicateTopicoException("Ya existe un tópico con el mismo título y mensaje."));

        mockMvc.perform(post(API_TOPICOS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTopicoCreateDTO)))
                .andExpect(status().isConflict());

        verify(topicoService).crearTopico(any(TopicoCreateDTO.class));
    }

    @Test
    @DisplayName("Should return 404 when autor not found")
    void crearTopicoAutorNotFoundReturnsNotFound() throws Exception {
        when(topicoService.crearTopico(any(TopicoCreateDTO.class)))
                .thenThrow(new ResourceNotFoundException("No se encontró el usuario con ID: 1"));

        mockMvc.perform(post(API_TOPICOS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTopicoCreateDTO)))
                .andExpect(status().isNotFound());

        verify(topicoService).crearTopico(any(TopicoCreateDTO.class));
    }

    @Test
    @DisplayName("Should return 404 when curso not found")
    void crearTopicoCursoNotFoundReturnsNotFound() throws Exception {
        when(topicoService.crearTopico(any(TopicoCreateDTO.class)))
                .thenThrow(new ResourceNotFoundException("No se encontró el curso con ID: 1"));

        mockMvc.perform(post(API_TOPICOS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTopicoCreateDTO)))
                .andExpect(status().isNotFound());

        verify(topicoService).crearTopico(any(TopicoCreateDTO.class));
    }

    @Test
    @DisplayName("Should handle invalid JSON gracefully")
    void crearTopicoInvalidJsonReturnsBadRequest() throws Exception {
        mockMvc.perform(post(API_TOPICOS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"invalid\": \"json\" }"))
                .andExpect(status().isBadRequest());

        verify(topicoService, never()).crearTopico(any());
    }

    @Test
    @DisplayName("Should handle service exception gracefully")
    void crearTopicoServiceThrowsExceptionReturnsInternalServerError() throws Exception {
        when(topicoService.crearTopico(any(TopicoCreateDTO.class)))
                .thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(post(API_TOPICOS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTopicoCreateDTO)))
                .andExpect(status().is5xxServerError());

        verify(topicoService).crearTopico(any(TopicoCreateDTO.class));
    }

    @Test
    @DisplayName("Should return 415 for unsupported media type")
    void crearTopicoUnsupportedMediaTypeReturnsUnsupportedMediaType() throws Exception {
        mockMvc.perform(post(API_TOPICOS)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("plain text"))
                .andExpect(status().isUnsupportedMediaType());

        verify(topicoService, never()).crearTopico(any());
    }

    @Test
    @DisplayName("Should get topico by id successfully")
    void obtenerTopicoPorIdReturnsOk() throws Exception {
        when(topicoService.obtenerTopicoPorId(1L)).thenReturn(successTopicoResponse);

        mockMvc.perform(get(API_TOPICOS + "/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.titulo").value("¿Cómo aprender Spring Boot?"))
                .andExpect(jsonPath("$.mensaje").value("Necesito recursos para aprender Spring Boot desde cero"));

        verify(topicoService).obtenerTopicoPorId(1L);
    }

    @Test
    @DisplayName("Should return 404 when topico not found by id")
    void obtenerTopicoPorIdNotFoundReturnsNotFound() throws Exception {
        when(topicoService.obtenerTopicoPorId(999L))
                .thenThrow(new ResourceNotFoundException("No se encontró el tópico con ID: 999"));

        mockMvc.perform(get(API_TOPICOS + "/{id}", 999L))
                .andExpect(status().isNotFound());

        verify(topicoService).obtenerTopicoPorId(999L);
    }

    @Test
    @DisplayName("Should list topicos without pagination params")
    void listarTopicosWithoutParamsReturnsOk() throws Exception {
        Page<TopicoResponseDTO> page = createMockPage(
                List.of(successTopicoResponse),
                0,
                10,
                1
        );

        when(topicoService.listarTopicos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get(API_TOPICOS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].titulo").value("¿Cómo aprender Spring Boot?"));

        verify(topicoService).listarTopicos(any(Pageable.class));
    }

    @Test
    @DisplayName("Should return paginated topicos with default page and size")
    void listarTopicosPaginatedDefaultParams() throws Exception {
        Page<TopicoResponseDTO> page = createMockPage(
                List.of(successTopicoResponse),
                0,
                10,
                1
        );

        when(topicoService.listarTopicos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get(API_TOPICOS)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true));

        verify(topicoService).listarTopicos(any(Pageable.class));
    }

    @Test
    @DisplayName("Should return empty page when no topicos found")
    void listarTopicosEmptyPageReturnsOk() throws Exception {
        Page<TopicoResponseDTO> emptyPage = createMockPage(
                List.of(),
                0,
                10,
                0
        );

        when(topicoService.listarTopicos(any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get(API_TOPICOS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.empty").value(true));

        verify(topicoService).listarTopicos(any(Pageable.class));
    }

    @Test
    @DisplayName("Should update topico successfully and return 200 OK")
    void actualizarTopicoValidRequestReturnsOk() throws Exception {
        TopicoResponseDTO updatedResponse = TopicoResponseDTO.builder()
                .withId(1L)
                .withTitulo("¿Cómo aprender Spring Boot? - Actualizado")
                .withMensaje("Necesito recursos actualizados para aprender Spring Boot")
                .withFechaCreacion(LocalDateTime.now())
                .build();

        when(topicoService.actualizarTopico(eq(1L), any(TopicoUpdateDTO.class))).thenReturn(updatedResponse);

        mockMvc.perform(put(API_TOPICOS + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTopicoUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.titulo").value("¿Cómo aprender Spring Boot? - Actualizado"))
                .andExpect(jsonPath("$.mensaje").value("Necesito recursos actualizados para aprender Spring Boot"));

        verify(topicoService).actualizarTopico(eq(1L), any(TopicoUpdateDTO.class));
    }

    @Test
    @DisplayName("Should return 400 for empty titulo in update")
    void actualizarTopicoEmptyTituloReturnsBadRequest() throws Exception {
        TopicoUpdateDTO invalidDTO = TopicoUpdateDTO.builder()
                .withTitulo("")
                .withMensaje("Mensaje válido")
                .withStatus(StatusTopico.CERRADO)
                .build();

        mockMvc.perform(put(API_TOPICOS + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(topicoService, never()).actualizarTopico(any(), any());
    }

    @Test
    @DisplayName("Should return 400 for empty mensaje in update")
    void actualizarTopicoEmptyMensajeReturnsBadRequest() throws Exception {
        TopicoUpdateDTO invalidDTO = TopicoUpdateDTO.builder()
                .withTitulo("Título válido")
                .withMensaje("")
                .withStatus(StatusTopico.CERRADO)
                .build();

        mockMvc.perform(put(API_TOPICOS + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(topicoService, never()).actualizarTopico(any(), any());
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent topico")
    void actualizarTopicoNotFoundReturnsNotFound() throws Exception {
        when(topicoService.actualizarTopico(eq(999L), any(TopicoUpdateDTO.class)))
                .thenThrow(new ResourceNotFoundException("No se encontró el tópico con ID: 999"));

        mockMvc.perform(put(API_TOPICOS + "/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTopicoUpdateDTO)))
                .andExpect(status().isNotFound());

        verify(topicoService).actualizarTopico(eq(999L), any(TopicoUpdateDTO.class));
    }

    @Test
    @DisplayName("Should delete topico successfully and return 204 NO CONTENT")
    void eliminarTopicoReturnsNoContent() throws Exception {
        doNothing().when(topicoService).eliminarTopico(1L);

        mockMvc.perform(delete(API_TOPICOS + "/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(topicoService).eliminarTopico(1L);
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent topico")
    void eliminarTopicoNotFoundReturnsNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("No se encontró el tópico con ID: 999"))
                .when(topicoService).eliminarTopico(999L);

        mockMvc.perform(delete(API_TOPICOS + "/{id}", 999L))
                .andExpect(status().isNotFound());

        verify(topicoService).eliminarTopico(999L);
    }

    private Page<TopicoResponseDTO> createMockPage(
            List<TopicoResponseDTO> content,
            int pageNumber,
            int pageSize,
            long totalElements
    ) {
        return new PageImpl<>(
                content,
                PageRequest.of(pageNumber, pageSize),
                totalElements
        );
    }
}
