package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class BookItemRequestDto {
	private long itemId;

	@FutureOrPresent
	private LocalDateTime start;

	private LocalDateTime end;
}
