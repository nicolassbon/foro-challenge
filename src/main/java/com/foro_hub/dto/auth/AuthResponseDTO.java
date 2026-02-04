package com.foro_hub.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder(setterPrefix = "with")
@Schema(description = "Respuesta al iniciar sesión exitosamente")
public record AuthResponseDTO(
        @Schema(description = "Token JWT para autenticación", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token,

        @Schema(description = "Tiempo de expiración del token en milisegundos", example = "3600000")
        Long expiresIn
) {
}

