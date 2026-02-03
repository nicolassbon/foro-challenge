package com.foro_hub.dto.curso;

import lombok.Builder;

@Builder(setterPrefix = "with")
public record CursoUpdateDTO(
        String nombre,
        String categoria
) {
}
