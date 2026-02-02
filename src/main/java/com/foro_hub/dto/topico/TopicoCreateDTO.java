package com.foro_hub.dto.topico;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record TopicoCreateDTO(
        @NotBlank(message = "El titulo es obligatorio")
        String titulo,

        @NotBlank(message = "El mensaje es obligatorio")
        String mensaje,

        @NotNull(message = "El id del curso es obligatorio")
        Long idCurso,

        @NotNull(message = "El id del autor es obligatorio")
        Long idAutor
) {
}
