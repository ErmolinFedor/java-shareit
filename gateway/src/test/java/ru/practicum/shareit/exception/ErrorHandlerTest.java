package ru.practicum.shareit.exception;

import jakarta.validation.ValidationException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exeption.ErrorHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ErrorHandlerTest {

  @Test
  void testErrorHandlerMethods() {
    ErrorHandler errorHandler = new ErrorHandler();

    IllegalArgumentException illegalArgException = new IllegalArgumentException("Unknown state: UNSUPPORTED");
    Map<String, String> responseArg = errorHandler.handleIllegalArgumentException(illegalArgException);

    assertNotNull(responseArg);
    assertEquals("Unknown state: UNSUPPORTED", responseArg.get("error"));

    ValidationException validationException = new ValidationException("Validation failed");
    Map<String, String> responseVal = errorHandler.handleValidationException(validationException);

    assertNotNull(responseVal);
    assertEquals("Validation failed", responseVal.get("error"));
  }
}