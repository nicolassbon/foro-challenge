package com.foro_hub.controller;

import com.foro_hub.dto.curso.CursoCreateDTO;
import com.foro_hub.dto.curso.CursoResponseDTO;
import com.foro_hub.dto.curso.CursoUpdateDTO;
import com.foro_hub.service.CursoService;
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
public class CursoController {

    private final CursoService cursoService;

    @PostMapping
    public ResponseEntity<CursoResponseDTO> crearCurso(@RequestBody @Valid final CursoCreateDTO createDTO, UriComponentsBuilder uriBuilder) {
        log.info("Creando nuevo curso con nombre: {}", createDTO.nombre());
        final CursoResponseDTO response = cursoService.crearCurso(createDTO);

        URI url = uriBuilder.path("/cursos/{id}").buildAndExpand(response.id()).toUri();

        log.info("Curso creado exitosamente con id: {}", response.id());
        return ResponseEntity.created(url).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CursoResponseDTO> obtenerCursoPorId(@PathVariable final Long id) {
        log.info("Buscando curso con id: {}", id);
        final CursoResponseDTO response = cursoService.obtenerCursoPorId(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<CursoResponseDTO>> listarCursos(@PageableDefault(sort = "nombre", direction = Sort.Direction.ASC) final Pageable pageable) {
        log.info("Listando cursos con paginacion: {}", pageable);

        final Page<CursoResponseDTO> response = cursoService.listarCursos(pageable);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CursoResponseDTO> actualizarCurso(
            @PathVariable final Long id,
            @RequestBody @Valid final CursoUpdateDTO updateDTO) {
        log.info("Actualizando curso con id: {}", id);
        final CursoResponseDTO response = cursoService.actualizarCurso(id, updateDTO);

        log.info("Curso actualizado exitosamente con id: {}", id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCurso(@PathVariable final Long id) {
        log.info("Eliminando curso con id: {}", id);
        cursoService.eliminarCurso(id);

        log.info("Curso eliminado exitosamente con id: {}", id);
        return ResponseEntity.noContent().build();
    }
}
