package com.neueda.assignment.urlshrinker.controller.advice;

import com.neueda.assignment.urlshrinker.repository.exception.URLNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Intercepts and handles common exceptions occurred when the application is trying to process requests.
 */
@ControllerAdvice
public class GeneralControllerAdvice {

    /**
     * Handles occurrences of {@link MethodArgumentNotValidException}, usually thrown when an argument annotated with
     * {@link javax.validation.Valid} fails to validate.
     *
     * @param exception Exception thrown when the application was trying to handle a request.
     *
     * @return A {@link HttpStatus#BAD_REQUEST} response containing a map with field name as key and message as value.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        Map<String, String> validationMessages = exception.getFieldErrors().stream()
            .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationMessages);
    }

    /**
     * Handles occurrences of {@link HttpMessageNotReadableException}, usually thrown when a request body is invalid.
     *
     * @param exception Exception thrown when the application was trying to handle a request.
     *
     * @return A {@link HttpStatus#BAD_REQUEST} response containing the message "Invalid request body.".
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        Map<String, String> validationMessage = Map.of("message", "Invalid request body.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationMessage);
    }

    /**
     * Handles occurrences of {@link URLNotFoundException}, usually thrown when a request using a non-existing alias
     * is received.
     *
     * @param exception Exception thrown when the application was trying to handle a request.
     *
     * @return A {@link HttpStatus#NOT_FOUND} response containing the exception message.
     */
    @ExceptionHandler(URLNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleURLNotFoundException(URLNotFoundException exception) {
        Map<String, String> validationMessage = Map.of("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(validationMessage);
    }

}
