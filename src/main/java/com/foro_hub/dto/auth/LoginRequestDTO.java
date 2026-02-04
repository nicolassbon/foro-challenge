package com.foro_hub.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Datos necesarios para iniciar sesión")
public record LoginRequestDTO(
        @Schema(description = "Email del usuario", example = "usuario@forohub.com")
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe ser válido")
        String email,

        @Schema(description = "Contraseña del usuario", example = "password123")
        @NotBlank(message = "La contraseña es obligatoria")
        String contrasena
) {
}

