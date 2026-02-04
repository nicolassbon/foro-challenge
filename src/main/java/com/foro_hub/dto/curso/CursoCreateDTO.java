package com.foro_hub.dto.curso;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Datos necesarios para crear un nuevo curso")
public record CursoCreateDTO(
        @Schema(description = "Nombre del curso", example = "Spring Boot Avanzado")
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @Schema(description = "Categoría del curso", example = "Backend")
        @NotBlank(message = "La categoria es obligatoria")
        String categoria
) {
}

