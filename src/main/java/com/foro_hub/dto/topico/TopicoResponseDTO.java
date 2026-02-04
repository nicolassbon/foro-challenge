package com.foro_hub.dto.topico;

import com.foro_hub.domain.enums.StatusTopico;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder(setterPrefix = "with")
@Schema(description = "Información completa de un tópico")
public record TopicoResponseDTO(
    @Schema(description = "ID único del tópico", example = "1")
    Long id,
    
    @Schema(description = "Título del tópico", example = "¿Cómo usar Spring Security con JWT?")
    String titulo,
    
    @Schema(description = "Mensaje o contenido del tópico", example = "Estoy intentando implementar autenticación JWT...")
    String mensaje,
    
    @Schema(description = "Fecha y hora de creación del tópico", example = "2026-02-04T15:30:00")
    LocalDateTime fechaCreacion,
    
    @Schema(description = "Estado actual del tópico", example = "ABIERTO")
    StatusTopico status
) {
}

