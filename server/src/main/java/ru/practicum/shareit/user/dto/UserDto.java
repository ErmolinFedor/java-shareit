package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {

  private Integer id;

  @NotBlank(message = "Имя не может быть пустым")
  private String name;

  @NotNull(message = "Email обязателен")
  @Email(message = "Некорректный формат email")
  private String email;
}
