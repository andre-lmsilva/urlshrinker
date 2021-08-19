package com.neueda.assignment.urlshrinker.controller.advice;


import com.neueda.assignment.urlshrinker.repository.exception.URLNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class GeneralControllerAdviceTest {

    private GeneralControllerAdvice controllerAdvice = new GeneralControllerAdvice();

    @Test
    @DisplayName("Given I intercept a MethodArgumentNotValidException, then I respond with BAD_REQUEST and the validation message per field.")
    void handleMethodArgumentNotValidException_withFieldErrors_returnsBadRequestAndValidationMessagesAsResponse() {
        FieldError fieldError = new FieldError("shortenURLRequest", "urlAddress",
            "must not be blank.");
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        doReturn(List.of(fieldError)).when(exception).getFieldErrors();

        ResponseEntity<Map<String, String>> result = this.controllerAdvice.handleMethodArgumentNotValidException(exception);

        assertThat(result).hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST);

        assertThat(result.getBody())
            .hasSize(1)
            .containsEntry("urlAddress", "must not be blank.");
    }

    @Test
    @DisplayName("Given I intercept a HttpMessageNotReadableException, then I respond with BAD_REQUEST and the 'Invalid request body.' message.")
    void handleHttpMessageNotReadableException_withNoOtherPreCondition_returnsBadRequestAndDefaultMessage() {
        ResponseEntity<Map<String, String>> result = this.controllerAdvice.handleHttpMessageNotReadableException(null);

        assertThat(result).hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST);
        assertThat(result.getBody())
            .hasSize(1)
            .containsEntry("message", "Invalid request body.");
    }

    @Test
    @DisplayName("Given I intercept an URLNotFoundException, then I respond with NOT_FOUND and the exception message.")
    void handleURLNotFoundException_withNoOtherPreCondition_returnsNotFoundAndExceptionMessage() {
        URLNotFoundException exception = new URLNotFoundException("Fake Message");

        ResponseEntity<Map<String, String>> result = this.controllerAdvice.handleURLNotFoundException(exception);

        assertThat(result).hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
        assertThat(result.getBody())
            .hasSize(1)
            .containsEntry("message", "Fake Message");
    }

}
