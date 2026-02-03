package com.foro_hub.dto.curso;

import jakarta.validation.constraints.NotBlank;

public record CursoCreateDTO(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "La categoria es obligatoria")
        String categoria
) {
}
