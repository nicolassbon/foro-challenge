package com.foro_hub.dto.curso;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder(setterPrefix = "with")
@Schema(description = "Información completa de un curso")
public record CursoResponseDTO(
        @Schema(description = "ID único del curso", example = "1")
        Long id,
        
        @Schema(description = "Nombre del curso", example = "Spring Boot Avanzado")
        String nombre,
        
        @Schema(description = "Categoría del curso", example = "Backend")
        String categoria
) {
}

