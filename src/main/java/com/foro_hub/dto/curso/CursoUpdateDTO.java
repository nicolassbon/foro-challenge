package com.foro_hub.dto.curso;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder(setterPrefix = "with")
@Schema(description = "Datos necesarios para actualizar un curso")
public record CursoUpdateDTO(
        @Schema(description = "Nombre actualizado del curso", example = "Spring Boot Avanzado - Nueva Edición")
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,
        
        @Schema(description = "Categoría actualizada del curso", example = "Backend")
        @NotBlank(message = "La categoria es obligatoria")
        String categoria
) {
}

