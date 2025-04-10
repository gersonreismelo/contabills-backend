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

/**
 * Manipulador global de exceções para as requisições REST.
 * 
 * Este componente intercepta exceções comuns lançadas nos controllers e retorna respostas apropriadas e padronizadas para a API.
 * 
 * <ul>
 * <li>Valida campos de entrada com {@link MethodArgumentNotValidException} e {@link ConstraintViolationException}</li>
 * <li>Lida com problemas de integridade do banco de dados com {@link DataIntegrityViolationException}</li>
 * <li>Intercepta erros de parsing de JSON com {@link HttpMessageNotReadableException}</li>
 * <li>Trata {@link ResponseStatusException} personalizada com código de status HTTP</li>
 * </ul>
 * 
 * @author Gerson
 * @version 1.0
 */
@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    /**
     * Construtor padrão.
     */
    public RestExceptionHandler() {
    }

    /**
     * Trata exceções de validação de argumentos em métodos (ex: @Valid com Bean Validation).
     *
     * @param e exceção contendo os campos inválidos
     * @return ResponseEntity com mensagem de erro e status 400
     */
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

    /**
     * Trata exceções lançadas manualmente com {@link ResponseStatusException}.
     *
     * @param e a exceção lançada contendo o código e a mensagem de erro
     * @return ResponseEntity com os detalhes do erro
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<RestError> responseStatusExceptionHandler(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode()).body(
                new RestError(e.getStatusCode().value(), e.getBody().getDetail()));
    }

    /**
     * Trata erros de leitura do corpo da requisição (ex: JSON mal formatado).
     *
     * @param e exceção de leitura de mensagem
     * @return ResponseEntity com mensagem de erro e status 400
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RestError> httpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(
                new RestError(400, "Campos inválidos"));
    }

    /**
     * Trata erros de violação de integridade no banco de dados (ex: chave duplicada).
     *
     * @param ex exceção de violação de integridade
     * @return ResponseEntity com mensagem de erro e status 409
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<RestError> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("Erro de violação de integridade de dados: {}", ex.getMessage());

        String errorMessage = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();

        return ResponseEntity.status(409).body(
                new RestError(409, "Erro de integridade de dados: " + errorMessage));
    }

    /**
     * Trata violações diretas de constraints, como anotações de validação @NotBlank, @Size, etc.
     *
     * @param e exceção de violação de constraint
     * @return ResponseEntity com mensagem de erro e status 400
     */
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
