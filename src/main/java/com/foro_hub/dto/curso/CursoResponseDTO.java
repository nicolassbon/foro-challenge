package com.foro_hub.dto.curso;

import lombok.Builder;

@Builder(setterPrefix = "with")
public record CursoResponseDTO(
        Long id,
        String nombre,
        String categoria
) {
}
