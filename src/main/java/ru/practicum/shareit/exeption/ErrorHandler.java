package ru.practicum.shareit.exeption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleNotFoundException(final NotFoundException e) {
    log.error("404: {}", e.getMessage());
    return new ErrorResponse(e.getMessage());
  }

  @ExceptionHandler(ValidationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleValidationException(final ValidationException e) {
    log.error("400: {}", e.getMessage());
    return new ErrorResponse(e.getMessage());
  }

  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ErrorResponse handleAccessDeniedException(final AccessDeniedException e) {
    log.error("403: {}", e.getMessage());
    return new ErrorResponse(e.getMessage());
  }

  @ExceptionHandler(ConflictException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorResponse handleConflictException(final AccessDeniedException e) {
    log.error("409: {}", e.getMessage());
    return new ErrorResponse(e.getMessage());
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleThrowable(final Throwable e) {
    log.error("Произошла непредвиденная ошибка: {}", e.getMessage(), e);
    return new ErrorResponse("Произошла непредвиденная ошибка.");
  }
}
