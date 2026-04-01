package me.learning.lmsplatform.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleNotFound(
      ResourceNotFoundException exception,
      HttpServletRequest request) {
    return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
      MethodArgumentNotValidException exception,
      HttpServletRequest request) {
    Map<String, String> validationErrors = exception.getBindingResult()
        .getFieldErrors()
        .stream()
        .collect(Collectors.toMap(
            FieldError::getField,
            fieldError -> fieldError.getDefaultMessage() == null
                ? "Validation failed"
                : fieldError.getDefaultMessage(),
            (first, second) -> first,
            LinkedHashMap::new));
    return buildResponse(
        HttpStatus.BAD_REQUEST,
        "Validation failed",
        request.getRequestURI(),
        validationErrors);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
      ConstraintViolationException exception,
      HttpServletRequest request) {
    Map<String, String> validationErrors = exception.getConstraintViolations()
        .stream()
        .collect(Collectors.toMap(
            violation -> violation.getPropertyPath().toString(),
            violation -> violation.getMessage() == null
                ? "Validation failed"
                : violation.getMessage(),
            (first, second) -> first,
            LinkedHashMap::new));
    return buildResponse(
        HttpStatus.BAD_REQUEST,
        "Validation failed",
        request.getRequestURI(),
        validationErrors);
  }

  @ExceptionHandler({
      IllegalArgumentException.class,
      MethodArgumentTypeMismatchException.class,
      HttpMessageNotReadableException.class,
      DataAccessException.class
  })
  public ResponseEntity<ApiErrorResponse> handleBadRequest(
      Exception exception,
      HttpServletRequest request) {
    log.warn("Request failed: {}", exception.getMessage(), exception);
    return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleUnexpected(
      Exception exception,
      HttpServletRequest request) {
    log.error("Unexpected error on {}", request.getRequestURI(), exception);
    return buildResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        exception.getMessage(),
        request.getRequestURI());
  }

  private ResponseEntity<ApiErrorResponse> buildResponse(
      HttpStatus status,
      String message,
      String path) {
    return buildResponse(status, message, path, Map.of());
  }

  private ResponseEntity<ApiErrorResponse> buildResponse(
      HttpStatus status,
      String message,
      String path,
      Map<String, String> errors) {
    ApiErrorResponse response = ApiErrorResponse.builder()
        .timestamp(Instant.now())
        .status(status.value())
        .error(status.getReasonPhrase())
        .message(message == null ? "Unexpected error" : message)
        .path(path)
        .errors(errors.isEmpty() ? null : errors)
        .build();
    return ResponseEntity.status(status).body(response);
  }
}
