package com.foro_hub.exception;

import com.foro_hub.dto.error.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFoundException(
            final ResourceNotFoundException ex) {

        log.error("RESOURCE_NOT_FOUND: {}", ex.getMessage());

        final ErrorResponseDTO error = ErrorResponseDTO.builder()
                .withTimestamp(LocalDateTime.now())
                .withStatus(HttpStatus.NOT_FOUND.value())
                .withError("Resource Not Found")
                .withMessage(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DuplicateTopicoException.class)
    public ResponseEntity<ErrorResponseDTO> handleDuplicateTopicoException(
            final DuplicateTopicoException ex) {

        log.error("DUPLICATE_TOPICO: {}", ex.getMessage());

        final ErrorResponseDTO error = ErrorResponseDTO.builder()
                .withTimestamp(LocalDateTime.now())
                .withStatus(HttpStatus.CONFLICT.value())
                .withError("Duplicate Resource")
                .withMessage(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            final MethodArgumentNotValidException ex) {

        log.error("VALIDATION_ERROR: {}", ex.getMessage());

        final Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            final String fieldName = ((FieldError) error).getField();
            final String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        final Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        response.put("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpMediaTypeNotSupportedException(
            final HttpMediaTypeNotSupportedException ex) {

        log.error("UNSUPPORTED_MEDIA_TYPE: {}", ex.getMessage());

        final ErrorResponseDTO error = ErrorResponseDTO.builder()
                .withTimestamp(LocalDateTime.now())
                .withStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .withError("Unsupported Media Type")
                .withMessage(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGlobalException(
            final Exception ex) {

        log.error("INTERNAL_ERROR: errorMessage: {}", ex.getMessage(), ex);

        final ErrorResponseDTO error = ErrorResponseDTO.builder()
                .withTimestamp(LocalDateTime.now())
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withError("Internal Server Error")
                .withMessage("Ha ocurrido un error inesperado")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
