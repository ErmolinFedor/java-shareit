package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.List;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestResponseDtoJsonTest {

  @Autowired
  private JacksonTester<ItemRequestResponseDto> json;

  @Test
  void testItemRequestResponseDtoSerialization() throws Exception {
    LocalDateTime time = LocalDateTime.of(2026, 5, 25, 12, 0, 0);
    ItemRequestResponseDto dto = ItemRequestResponseDto.builder()
        .id(1)
        .description("Ищу стремянку")
        .created(time)
        .items(List.of())
        .build();

    JsonContent<ItemRequestResponseDto> result = json.write(dto);

    assertThat(result).hasJsonPathNumberValue("$.id");
    assertThat(result).hasJsonPathStringValue("$.description");
    assertThat(result).hasJsonPathStringValue("$.created");
    assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Ищу стремянку");
    assertThat(result).extractingJsonPathStringValue("$.created").contains("2026-05-25T12:00:00");
  }
}
