package com.foro_hub.dto.topico;

import com.foro_hub.domain.enums.StatusTopico;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder(setterPrefix = "with")
@Schema(description = "Datos necesarios para actualizar un tópico")
public record TopicoUpdateDTO(
        @Schema(description = "Título actualizado del tópico", example = "¿Cómo usar Spring Security con JWT? [RESUELTO]")
        @NotBlank(message = "El titulo es obligatorio")
        String titulo,

        @Schema(description = "Mensaje o contenido actualizado del tópico", example = "Ya logré configurar JWT correctamente siguiendo la documentación oficial...")
        @NotBlank(message = "El mensaje es obligatorio")
        String mensaje,

        @Schema(description = "Estado del tópico", example = "CERRADO", allowableValues = {"ABIERTO", "CERRADO", "RESUELTO"})
        StatusTopico status
) {
}
