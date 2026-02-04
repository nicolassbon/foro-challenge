package com.foro_hub.controller;

import com.foro_hub.dto.curso.CursoCreateDTO;
import com.foro_hub.dto.curso.CursoResponseDTO;
import com.foro_hub.dto.curso.CursoUpdateDTO;
import com.foro_hub.security.JwtService;
import com.foro_hub.service.CursoService;
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

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CursoController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJsonTesters
class CursoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JacksonTester<CursoCreateDTO> cursoCreateJson;

    @Autowired
    private JacksonTester<CursoUpdateDTO> cursoUpdateJson;

    @Autowired
    private JacksonTester<CursoResponseDTO> cursoResponseJson;

    @MockitoBean
    private CursoService cursoService;

    // Mocking JwtService to bypass security filters
    @MockitoBean
    private JwtService jwtService;

    private CursoResponseDTO cursoResponse;

    @BeforeEach
    void setUp() {
        cursoResponse = CursoResponseDTO.builder()
                .withId(1L)
                .withNombre("Spring Boot Avanzado")
                .withCategoria("Backend")
                .build();
    }

    @Test
    @DisplayName("POST /cursos - Debería retornar 201 Created, Header Location y Body correcto")
    void crearCurso_DeberiaRetornar201() throws Exception {
        // GIVEN
        CursoCreateDTO requestDto = new CursoCreateDTO("Spring Boot Avanzado", "Backend");
        given(cursoService.crearCurso(any())).willReturn(cursoResponse);

        // WHEN
        MockHttpServletResponse response = mockMvc.perform(post("/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cursoCreateJson.write(requestDto).getJson()))
                .andReturn().getResponse();

        // THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getHeader("Location")).contains("/cursos/1");
        assertThat(cursoResponseJson.parse(response.getContentAsString())).usingRecursiveComparison()
                .isEqualTo(cursoResponse);
    }

    @Test
    @DisplayName("POST /cursos - Debería retornar 400 Bad Request si falta el nombre")
    void crearCurso_ConNombreNulo_Retorna400() throws Exception {
        // GIVEN
        CursoCreateDTO invalidDto = new CursoCreateDTO(null, "Backend");

        // WHEN
        MockHttpServletResponse response = mockMvc.perform(post("/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cursoCreateJson.write(invalidDto).getJson()))
                .andReturn().getResponse();

        // THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("POST /cursos - Debería retornar 400 Bad Request si falta la categoria")
    void crearCurso_ConCategoriaNula_Retorna400() throws Exception {
        // GIVEN
        CursoCreateDTO invalidDto = new CursoCreateDTO("Spring Boot Avanzado", null);

        // WHEN & THEN
        mockMvc.perform(post("/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cursoCreateJson.write(invalidDto).getJson()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /cursos - Debería retornar 400 Bad Request si campos están vacíos")
    void crearCurso_ConCamposVacios_Retorna400() throws Exception {
        // GIVEN
        CursoCreateDTO invalidDto = new CursoCreateDTO("", "");

        // WHEN & THEN
        mockMvc.perform(post("/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cursoCreateJson.write(invalidDto).getJson()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /cursos/{id} - Debería retornar 200 y el DTO")
    void obtenerCurso_Retorna200() throws Exception {
        // GIVEN
        given(cursoService.obtenerCursoPorId(1L)).willReturn(cursoResponse);

        // WHEN
        MockHttpServletResponse response = mockMvc.perform(get("/cursos/{id}", 1L))
                .andReturn().getResponse();

        // THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(cursoResponseJson.parse(response.getContentAsString())).usingRecursiveComparison()
                .isEqualTo(cursoResponse);
    }

    @Test
    @DisplayName("GET /cursos - Debería retornar Paginación correctamente")
    void listarCursos_RetornaPage() throws Exception {
        // GIVEN
        Page<CursoResponseDTO> page = new PageImpl<>(Collections.singletonList(cursoResponse));
        given(cursoService.listarCursos(any(Pageable.class))).willReturn(page);

        // WHEN
        MockHttpServletResponse response = mockMvc.perform(get("/cursos")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "nombre,asc"))
                .andReturn().getResponse();

        // THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains("Spring Boot Avanzado");
        assertThat(response.getContentAsString()).contains("\"pageable\"");
    }

    @Test
    @DisplayName("PUT /cursos/{id} - Debería retornar 200 y el DTO actualizado")
    void actualizarCurso_Retorna200() throws Exception {
        // GIVEN
        CursoUpdateDTO updateDto = CursoUpdateDTO.builder()
                .withNombre("Spring Boot Avanzado - Actualizado")
                .withCategoria("Backend & Microservicios")
                .build();

        CursoResponseDTO updatedResponse = CursoResponseDTO.builder()
                .withId(1L)
                .withNombre("Spring Boot Avanzado - Actualizado")
                .withCategoria("Backend & Microservicios")
                .build();

        given(cursoService.actualizarCurso(eq(1L), any(CursoUpdateDTO.class))).willReturn(updatedResponse);

        // WHEN
        MockHttpServletResponse response = mockMvc.perform(put("/cursos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cursoUpdateJson.write(updateDto).getJson()))
                .andReturn().getResponse();

        // THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(cursoResponseJson.parse(response.getContentAsString())).usingRecursiveComparison()
                .isEqualTo(updatedResponse);
    }

    @Test
    @DisplayName("DELETE /cursos/{id} - Debería retornar 204 No Content")
    void eliminarCurso_Retorna204() throws Exception {
        // GIVEN
        doNothing().when(cursoService).eliminarCurso(1L);

        // WHEN
        MockHttpServletResponse response = mockMvc.perform(delete("/cursos/{id}", 1L))
                .andReturn().getResponse();

        // THEN
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("POST /cursos - Debería manejar JSON inválido")
    void crearCurso_ConJsonInvalido_Retorna400() throws Exception {
        // WHEN & THEN
        mockMvc.perform(post("/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"invalid\": \"json\" }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /cursos - Debería manejar Content-Type no soportado")
    void crearCurso_ConContentTypeNoSoportado_Retorna415() throws Exception {
        // WHEN & THEN
        mockMvc.perform(post("/cursos")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("plain text"))
                .andExpect(status().isUnsupportedMediaType());
    }
}
