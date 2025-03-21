package br.com.contabills.config;

import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import br.com.contabills.exceptions.RestError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestError> argumentExceptionHandler(MethodArgumentNotValidException e) {
        log.error("Erro de argumento inválido");
        StringBuilder errorMessage = new StringBuilder("Campos inválidos: ");
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errorMessage.append(fieldError.getField())
                    .append(" (")
                    .append(fieldError.getDefaultMessage())
                    .append("), ");
        }
        return ResponseEntity.badRequest().body(
                new RestError(400, errorMessage.toString()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<RestError> responseStatusExceptionHandler(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode()).body(
                new RestError(e.getStatusCode().value(), e.getBody().getDetail()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RestError> httpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(
                new RestError(400, "Campos inválidos"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<RestError> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("Erro de violação de integridade de dados: {}", ex.getMessage());

        String errorMessage = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();

        return ResponseEntity.status(409).body(
                new RestError(409, "Erro de integridade de dados: " + errorMessage));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<RestError> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("Erro de validação de dados: {}", e.getMessage());
        StringBuilder errorMessage = new StringBuilder("Erro de validação: ");
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            errorMessage.append(violation.getPropertyPath())
                    .append(" - ")
                    .append(violation.getMessage())
                    .append("; ");
        }
        return ResponseEntity.badRequest().body(
                new RestError(400, errorMessage.toString()));
    }

}
