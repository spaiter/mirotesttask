package com.miro.api.widgets.testtask.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Global API exception handler. Allow to convert app exceptions to human readable response.
 */
@ControllerAdvice
public class ExceptionsHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> details = ex
                .getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String message = error.getDefaultMessage();
                    return String.format("%s - %s", fieldName, message);
                })
                .collect(Collectors.toList());
        ErrorResponse error = new ErrorResponse("Validation Failed.", details);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleMissingParams(ConstraintViolationException ex) {
        List<String> details = ex
                .getConstraintViolations()
                .stream()
                .map(violation -> {
                    String fieldName = violation.getPropertyPath().toString().split("\\.")[1];
                    String message = violation.getMessage();
                    return String.format("%s - %s", fieldName, message);
                })
                .collect(Collectors.toList());
        ErrorResponse error = new ErrorResponse("Invalid query params.", details);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}