package com.foro_hub.dto.topico;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder(setterPrefix = "with")
@Schema(description = "Datos necesarios para crear un nuevo tópico")
public record TopicoCreateDTO(
        @Schema(description = "Título del tópico", example = "¿Cómo usar Spring Security con JWT?")
        @NotBlank(message = "El titulo es obligatorio")
        String titulo,

        @Schema(description = "Mensaje o contenido del tópico", example = "Estoy intentando implementar autenticación JWT en mi proyecto Spring Boot pero no logro configurarlo correctamente...")
        @NotBlank(message = "El mensaje es obligatorio")
        String mensaje,

        @Schema(description = "ID del curso al que pertenece el tópico", example = "1")
        @NotNull(message = "El id del curso es obligatorio")
        Long idCurso
) {
}

