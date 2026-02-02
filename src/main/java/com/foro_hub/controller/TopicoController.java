package com.foro_hub.controller;

import com.foro_hub.dto.topico.TopicoCreateDTO;
import com.foro_hub.dto.topico.TopicoResponseDTO;
import com.foro_hub.dto.topico.TopicoUpdateDTO;
import com.foro_hub.service.TopicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/topicos")
@RequiredArgsConstructor
@Slf4j
public class TopicoController {

    private final TopicoService topicoService;

    @PostMapping
    public ResponseEntity<TopicoResponseDTO> crearTopico(@RequestBody @Valid final TopicoCreateDTO dto) {
        log.info("Creando nuevo topico con titulo: {}", dto.titulo());
        final TopicoResponseDTO response = topicoService.crearTopico(dto);

        log.info("Topico creado exitosamente con id: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicoResponseDTO> obtenerTopicoPorId(@PathVariable final Long id) {
        log.info("Obteniendo topico con id: {}", id);
        final TopicoResponseDTO response = topicoService.obtenerTopicoPorId(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<TopicoResponseDTO>> listarTopicos(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size,
            @RequestParam(defaultValue = "fechaCreacion") final String sort,
            @RequestParam(defaultValue = "ASC") final Sort.Direction direction) {

        log.info("Listando topicos con paginacion - page: {}, size: {}, sort: {}, direction: {}",
                page, size, sort, direction);

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        final Page<TopicoResponseDTO> response = topicoService.listarTopicos(pageable);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TopicoResponseDTO> actualizarTopico(
            @PathVariable final Long id,
            @RequestBody @Valid final TopicoUpdateDTO dto) {
        log.info("Actualizando topico con id: {}", id);
        final TopicoResponseDTO response = topicoService.actualizarTopico(id, dto);

        log.info("Topico con id: {}, actualizado exitosamente", id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTopico(@PathVariable final Long id) {
        log.info("Eliminando topico con id: {}", id);
        topicoService.eliminarTopico(id);

        log.info("Topico con id: {}, eliminado exitosamente", id);
        return ResponseEntity.noContent().build();
    }
}
