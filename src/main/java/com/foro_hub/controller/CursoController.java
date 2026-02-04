package com.foro_hub.controller;

import com.foro_hub.dto.curso.CursoCreateDTO;
import com.foro_hub.dto.curso.CursoResponseDTO;
import com.foro_hub.dto.curso.CursoUpdateDTO;
import com.foro_hub.dto.error.ErrorResponseDTO;
import com.foro_hub.service.CursoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/cursos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cursos", description = "Gestión de cursos del foro (requiere autenticación)")
@SecurityRequirement(name = "bearerAuth")
public class CursoController {

    private final CursoService cursoService;

    @Operation(
            summary = "Crear nuevo curso",
            description = "Registra un nuevo curso en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Curso creado exitosamente",
                    content = @Content(schema = @Schema(implementation = CursoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No autenticado"
            )
    })
    @PostMapping
    public ResponseEntity<CursoResponseDTO> crearCurso(@RequestBody @Valid final CursoCreateDTO createDTO, UriComponentsBuilder uriBuilder) {
        log.info("Creando nuevo curso con nombre: {}", createDTO.nombre());
        final CursoResponseDTO response = cursoService.crearCurso(createDTO);

        URI url = uriBuilder.path("/cursos/{id}").buildAndExpand(response.id()).toUri();

        log.info("Curso creado exitosamente con id: {}", response.id());
        return ResponseEntity.created(url).body(response);
    }

    @Operation(
            summary = "Obtener curso por ID",
            description = "Retorna la información detallada de un curso específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Curso encontrado",
                    content = @Content(schema = @Schema(implementation = CursoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No autenticado"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Curso no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<CursoResponseDTO> obtenerCursoPorId(
            @Parameter(description = "ID del curso", example = "1")
            @PathVariable final Long id) {
        log.info("Buscando curso con id: {}", id);
        final CursoResponseDTO response = cursoService.obtenerCursoPorId(id);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Listar todos los cursos",
            description = "Retorna una lista paginada de todos los cursos activos ordenados por nombre."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de cursos recuperada exitosamente"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No autenticado"
            )
    })
    @GetMapping
    public ResponseEntity<Page<CursoResponseDTO>> listarCursos(
            @Parameter(description = "Parámetros de paginación y ordenamiento", example = "page=0&size=10&sort=nombre,asc")
            @PageableDefault(sort = "nombre", direction = Sort.Direction.ASC) final Pageable pageable) {
        log.info("Listando cursos con paginacion: {}", pageable);

        final Page<CursoResponseDTO> response = cursoService.listarCursos(pageable);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Actualizar curso",
            description = "Actualiza la información de un curso existente."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Curso actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = CursoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No autenticado"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Curso no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<CursoResponseDTO> actualizarCurso(
            @Parameter(description = "ID del curso a actualizar", example = "1")
            @PathVariable final Long id,
            @RequestBody @Valid final CursoUpdateDTO updateDTO) {
        log.info("Actualizando curso con id: {}", id);

        final CursoResponseDTO response = cursoService.actualizarCurso(id, updateDTO);
        log.info("Curso actualizado exitosamente con id: {}", id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Eliminar curso",
            description = "Elimina un curso del sistema (eliminación lógica)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Curso eliminado exitosamente"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No autenticado"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Curso no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCurso(
            @Parameter(description = "ID del curso a eliminar", example = "1")
            @PathVariable final Long id) {
        log.info("Eliminando curso con id: {}", id);
        cursoService.eliminarCurso(id);

        log.info("Curso eliminado exitosamente con id: {}", id);
        return ResponseEntity.noContent().build();
    }
}

