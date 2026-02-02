package com.foro_hub.dto.topico;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder(setterPrefix = "with")
public record TopicoResponseDTO(
    Long id,
    String titulo,
    String mensaje,
    LocalDateTime fechaCreacion
) {
}
