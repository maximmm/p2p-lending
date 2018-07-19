package com.wandoo.core.configuration;

import com.wandoo.core.validation.ValidationException;
import com.wandoo.core.validation.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static java.lang.String.format;
import static org.springframework.http.ResponseEntity.badRequest;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    protected ResponseEntity<?> handleValidationException(ValidationException exception) {
        return badRequest().body(exception.getValidationResult());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<?> handleException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return badRequest().body(format("ERROR: %s", exception.getMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ValidationResult validationResult = new ValidationResult();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> validationResult.addError(error.getField(), error.getDefaultMessage()));
        return badRequest().body(validationResult);
    }

}
