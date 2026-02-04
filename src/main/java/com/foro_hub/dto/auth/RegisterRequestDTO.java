package com.foro_hub.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos necesarios para registrar un nuevo usuario")
public record RegisterRequestDTO(
        @Schema(description = "Nombre completo del usuario", example = "Usuario Test")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        String nombre,

        @Schema(description = "Email del usuario (debe ser único)", example = "usuario@forohub.com")
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe ser válido")
        @Size(max = 150, message = "El email no debe exceder 150 caracteres")
        String email,

        @Schema(description = "Contraseña del usuario", example = "password123")
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
        String contrasena
) {
}

