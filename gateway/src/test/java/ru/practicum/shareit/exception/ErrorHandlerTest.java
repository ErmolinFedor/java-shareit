package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exeption.ConflictException;
import ru.practicum.shareit.exeption.ErrorHandler;
import ru.practicum.shareit.exeption.ErrorResponse;
import ru.practicum.shareit.exeption.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ErrorHandlerTest {

  @Test
  void testErrorHandlerMethods() {
    ErrorHandler errorHandler = new ErrorHandler();

    ConflictException conflictException = new ConflictException(
        "error");
    ErrorResponse responseConf = errorHandler.handleThrowable(conflictException);

    assertNotNull(responseConf);
    assertEquals("Произошла непредвиденная ошибка.", responseConf.error());

    ValidationException validationException = new ValidationException(
        "error");
    ErrorResponse responseVal = errorHandler.handleThrowable(validationException);

    assertNotNull(responseVal);
    assertEquals("Произошла непредвиденная ошибка.", responseVal.error());
  }
}