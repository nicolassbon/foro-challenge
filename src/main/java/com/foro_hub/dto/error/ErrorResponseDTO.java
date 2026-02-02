package com.foro_hub.dto.error;
import lombok.Builder;
import java.time.LocalDateTime;

@Builder(setterPrefix = "with")
public record ErrorResponseDTO(
        LocalDateTime timestamp,
        Integer status,
        String error,
        String message
) {
}
