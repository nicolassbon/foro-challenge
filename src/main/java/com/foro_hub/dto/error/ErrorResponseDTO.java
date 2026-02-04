package com.foro_hub.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Builder(setterPrefix = "with")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Información del error cuando ocurre un problema en la API")
public record ErrorResponseDTO(
        @Schema(description = "Fecha y hora en que ocurrió el error", example = "2026-02-04T15:30:00")
        LocalDateTime timestamp,
        
        @Schema(description = "Código de estado HTTP", example = "400")
        int status,
        
        @Schema(description = "Tipo de error", example = "Bad Request")
        String error,
        
        @Schema(description = "Mensaje descriptivo del error", example = "Error en la validación de campos")
        String message,
        
        @Schema(description = "Mapa de errores de validación por campo (solo presente en errores de validación)")
        Map<String, String> validationErrors
) {
}
