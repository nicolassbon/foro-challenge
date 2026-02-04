package com.foro_hub.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder(setterPrefix = "with")
@Schema(description = "Respuesta al registrar un nuevo usuario")
public record RegisterResponseDTO(
        @Schema(description = "ID único del usuario registrado", example = "1")
        Long id,
        @Schema(description = "Nombre completo del usuario registrado", example = "Usuario Test")
        String nombre,
        @Schema(description = "Email del usuario registrado", example = "usuario@forohub.com")
        String email
) {
}
