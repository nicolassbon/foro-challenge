package com.foro_hub.controller;

import com.foro_hub.dto.error.ErrorResponseDTO;
import com.foro_hub.dto.topico.TopicoCreateDTO;
import com.foro_hub.dto.topico.TopicoResponseDTO;
import com.foro_hub.dto.topico.TopicoUpdateDTO;
import com.foro_hub.service.TopicoService;
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
@RequestMapping("/topicos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tópicos", description = "Gestión de tópicos del foro (requiere autenticación)")
@SecurityRequirement(name = "bearerAuth")
public class TopicoController {

    private final TopicoService topicoService;

    @Operation(
            summary = "Crear nuevo tópico",
            description = "Crea un nuevo tópico en el foro. El usuario autenticado será registrado como autor del tópico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Tópico creado exitosamente",
                    content = @Content(schema = @Schema(implementation = TopicoResponseDTO.class))
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
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe un tópico con el mismo título y mensaje",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PostMapping
    public ResponseEntity<TopicoResponseDTO> crearTopico(@RequestBody @Valid final TopicoCreateDTO createDTO, UriComponentsBuilder uriBuilder) {
        log.info("Creando nuevo topico con titulo: {}", createDTO.titulo());
        final TopicoResponseDTO response = topicoService.crearTopico(createDTO);

        URI url = uriBuilder.path("/topicos/{id}").buildAndExpand(response.id()).toUri();

        log.info("Topico creado exitosamente con id: {}", response.id());
        return ResponseEntity.created(url).body(response);
    }

    @Operation(
            summary = "Obtener tópico por ID",
            description = "Retorna la información detallada de un tópico específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tópico encontrado",
                    content = @Content(schema = @Schema(implementation = TopicoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No autenticado"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tópico no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<TopicoResponseDTO> obtenerTopicoPorId(
            @Parameter(description = "ID del tópico", example = "1")
            @PathVariable final Long id) {
        log.info("Obteniendo topico con id: {}", id);
        final TopicoResponseDTO response = topicoService.obtenerTopicoPorId(id);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Listar todos los tópicos",
            description = "Retorna una lista paginada de todos los tópicos activos ordenados por fecha de creación."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de tópicos recuperada exitosamente"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No autenticado"
            )
    })
    @GetMapping
    public ResponseEntity<Page<TopicoResponseDTO>> listarTopicos(
            @Parameter(description = "Parámetros de paginación y ordenamiento", example = "page=0&size=10&sort=fechaCreacion,asc")
            @PageableDefault(sort = "fechaCreacion", direction = Sort.Direction.ASC) final Pageable pageable) {
        log.info("Listando topicos con paginacion: {}", pageable);

        final Page<TopicoResponseDTO> response = topicoService.listarTopicos(pageable);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Actualizar tópico",
            description = "Actualiza la información de un tópico existente."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tópico actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = TopicoResponseDTO.class))
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
                    description = "Tópico no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe otro tópico con el mismo título y mensaje",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<TopicoResponseDTO> actualizarTopico(
            @Parameter(description = "ID del tópico a actualizar", example = "1")
            @PathVariable final Long id,
            @RequestBody @Valid final TopicoUpdateDTO dto) {
        log.info("Actualizando topico con id: {}", id);
        final TopicoResponseDTO response = topicoService.actualizarTopico(id, dto);

        log.info("Topico con id: {}, actualizado exitosamente", id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Eliminar tópico",
            description = "Elimina un tópico del sistema (eliminación lógica)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Tópico eliminado exitosamente"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No autenticado"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tópico no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTopico(
            @Parameter(description = "ID del tópico a eliminar", example = "1")
            @PathVariable final Long id) {
        log.info("Eliminando topico con id: {}", id);
        topicoService.eliminarTopico(id);

        log.info("Topico con id: {}, eliminado exitosamente", id);
        return ResponseEntity.noContent().build();
    }
}
