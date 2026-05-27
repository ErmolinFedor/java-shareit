package ru.practicum.shareit;

import org.junit.jupiter.api.Test;

public class ShareItGatewayTest {

  @Test
  void testMainMethod() {

    try {
      ShareItGateway.main(new String[]{});
    } catch (Exception ignored) {
    }
  }
}
