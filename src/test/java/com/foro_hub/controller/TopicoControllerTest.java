package com.foro_hub.controller;

import com.foro_hub.domain.enums.StatusTopico;
import com.foro_hub.dto.topico.TopicoCreateDTO;
import com.foro_hub.dto.topico.TopicoResponseDTO;
import com.foro_hub.dto.topico.TopicoUpdateDTO;
import com.foro_hub.security.JwtService;
import com.foro_hub.service.TopicoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TopicoController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJsonTesters
class TopicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JacksonTester<TopicoCreateDTO> topicoCreateJson;

    @Autowired
    private JacksonTester<TopicoUpdateDTO> topicoUpdateJson;

    @Autowired
    private JacksonTester<TopicoResponseDTO> topicoResponseJson;

    @MockitoBean
    private TopicoService topicoService;

    // Mocking JwtService to bypass security filters
    @MockitoBean
    private JwtService jwtService;


    private TopicoResponseDTO topicoResponse;

    @BeforeEach
    void setUp() {
        topicoResponse = TopicoResponseDTO.builder()
                .withId(1L)
                .withTitulo("Titulo Demo")
                .withMensaje("Mensaje Demo")
                .withStatus(StatusTopico.ABIERTO)
                .withFechaCreacion(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("POST /topicos - Debería retornar 201 Created, Header Location y Body correcto")
    void crearTopico_DeberiaRetornar201() throws Exception {
        // GIVEN
        TopicoCreateDTO requestDto = new TopicoCreateDTO("Titulo Demo", "Mensaje Demo", 1L);
        given(topicoService.crearTopico(any())).willReturn(topicoResponse);

        // WHEN
        MockHttpServletResponse response = mockMvc.perform(post("/topicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(topicoCreateJson.write(requestDto).getJson()))
                .andReturn().getResponse();

        // THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getHeader("Location")).contains("/topicos/1");
        assertThat(topicoResponseJson.parse(response.getContentAsString())).usingRecursiveComparison()
                .isEqualTo(topicoResponse);
    }

    @Test
    @DisplayName("POST /topicos - Debería retornar 400 Bad Request si falta el título")
    void crearTopico_ConTituloNulo_Retorna400() throws Exception {
        // GIVEN
        TopicoCreateDTO invalidDto = new TopicoCreateDTO(null, "Mensaje Demo", 1L);

        // WHEN
        MockHttpServletResponse response = mockMvc.perform(post("/topicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(topicoCreateJson.write(invalidDto).getJson()))
                .andReturn().getResponse();

        // THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("POST /topicos - Debería retornar 400 Bad Request si falta el mensaje")
    void crearTopico_ConMensajeNulo_Retorna400() throws Exception {
        // GIVEN
        TopicoCreateDTO invalidDto = new TopicoCreateDTO("Titulo Demo", null, 1L);

        // WHEN & THEN
        mockMvc.perform(post("/topicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(topicoCreateJson.write(invalidDto).getJson()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /topicos - Debería retornar 400 Bad Request si campos están vacíos")
    void crearTopico_ConCamposVacios_Retorna400() throws Exception {
        // GIVEN
        TopicoCreateDTO invalidDto = new TopicoCreateDTO("", "", 1L);

        // WHEN & THEN
        mockMvc.perform(post("/topicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(topicoCreateJson.write(invalidDto).getJson()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /topicos/{id} - Debería retornar 200 y el DTO")
    void obtenerTopico_Retorna200() throws Exception {
        // GIVEN
        given(topicoService.obtenerTopicoPorId(1L)).willReturn(topicoResponse);

        // WHEN
        MockHttpServletResponse response = mockMvc.perform(get("/topicos/{id}", 1L))
                .andReturn().getResponse();

        // THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        assertThat(topicoResponseJson.parse(response.getContentAsString())).usingRecursiveComparison()
                .isEqualTo(topicoResponse);
    }

    @Test
    @DisplayName("GET /topicos - Debería retornar Paginación correctamente")
    void listarTopicos_RetornaPage() throws Exception {
        // GIVEN
        Page<TopicoResponseDTO> page = new PageImpl<>(Collections.singletonList(topicoResponse));
        given(topicoService.listarTopicos(any(Pageable.class))).willReturn(page);

        // WHEN
        MockHttpServletResponse response = mockMvc.perform(get("/topicos")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "fechaCreacion,asc"))
                .andReturn().getResponse();

        // THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains("Titulo Demo");
        assertThat(response.getContentAsString()).contains("\"pageable\"");
    }

    @Test
    @DisplayName("PUT /topicos/{id} - Debería retornar 200 y el DTO actualizado")
    void actualizarTopico_Retorna200() throws Exception {
        // GIVEN
        TopicoUpdateDTO updateDto = TopicoUpdateDTO.builder()
                .withTitulo("Titulo Demo - Actualizado")
                .withMensaje("Mensaje Demo - Actualizado")
                .withStatus(StatusTopico.CERRADO)
                .build();

        TopicoResponseDTO updatedResponse = TopicoResponseDTO.builder()
                .withId(1L)
                .withTitulo("Titulo Demo - Actualizado")
                .withMensaje("Mensaje Demo - Actualizado")
                .withStatus(StatusTopico.CERRADO)
                .withFechaCreacion(LocalDateTime.now())
                .build();

        given(topicoService.actualizarTopico(eq(1L), any(TopicoUpdateDTO.class))).willReturn(updatedResponse);

        // WHEN
        MockHttpServletResponse response = mockMvc.perform(put("/topicos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(topicoUpdateJson.write(updateDto).getJson()))
                .andReturn().getResponse();

        // THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(topicoResponseJson.parse(response.getContentAsString())).usingRecursiveComparison()
                .isEqualTo(updatedResponse);
    }

    @Test
    @DisplayName("DELETE /topicos/{id} - Debería retornar 204 No Content")
    void eliminarTopico_Retorna204() throws Exception {
        // GIVEN
        doNothing().when(topicoService).eliminarTopico(1L);

        // WHEN
        MockHttpServletResponse response = mockMvc.perform(delete("/topicos/{id}", 1L))
                .andReturn().getResponse();

        // THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("POST /topicos - Debería manejar JSON inválido")
    void crearTopico_ConJsonInvalido_Retorna400() throws Exception {
        // WHEN & THEN
        mockMvc.perform(post("/topicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"invalid\": \"json\" }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /topicos - Debería manejar Content-Type no soportado")
    void crearTopico_ConContentTypeNoSoportado_Retorna415() throws Exception {
        // WHEN & THEN
        mockMvc.perform(post("/topicos")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("plain text"))
                .andExpect(status().isUnsupportedMediaType());
    }
}
