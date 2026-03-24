package me.learning.lmsplatform.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleNotFound(
      ResourceNotFoundException exception,
      HttpServletRequest request) {
    return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler({
      IllegalArgumentException.class,
      MethodArgumentTypeMismatchException.class,
      DataAccessException.class
  })
  public ResponseEntity<Map<String, Object>> handleBadRequest(
      Exception exception,
      HttpServletRequest request) {
    log.warn("Request failed: {}", exception.getMessage(), exception);
    return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleUnexpected(
      Exception exception,
      HttpServletRequest request) {
    log.error("Unexpected error on {}", request.getRequestURI(), exception);
    return buildResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        exception.getMessage(),
        request.getRequestURI());
  }

  private ResponseEntity<Map<String, Object>> buildResponse(
      HttpStatus status,
      String message,
      String path) {
    return ResponseEntity.status(status).body(Map.of(
        "status", status.value(),
        "error", status.getReasonPhrase(),
        "message", message == null ? "Unexpected error" : message,
        "path", path));
  }
}
