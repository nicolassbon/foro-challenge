package com.foro_hub.dto.topico;
import com.foro_hub.domain.enums.StatusTopico;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
@Builder(setterPrefix = "with")
public record TopicoUpdateDTO(
        @NotBlank(message = "El titulo es obligatorio")
        String titulo,
        @NotBlank(message = "El mensaje es obligatorio")
        String mensaje,
        StatusTopico status
) {
}
