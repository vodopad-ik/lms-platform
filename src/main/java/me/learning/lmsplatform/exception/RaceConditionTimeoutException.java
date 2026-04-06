package me.learning.lmsplatform.exception;

public class RaceConditionTimeoutException extends RuntimeException {
  
  public RaceConditionTimeoutException(String message) {
    super(message);
  }
  
  public RaceConditionTimeoutException(String message, Throwable cause) {
    super(message, cause);
  }
}
