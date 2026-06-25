package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
	@NotNull
	@Positive
	private Long itemId;

	@NotNull
	@FutureOrPresent
	private LocalDateTime start;

	@NotNull
	@Future
	private LocalDateTime end;

	@AssertTrue(message = "Start must be before end")
	public boolean isStartBeforeEnd() {
		return start == null || end == null || start.isBefore(end);
	}
}
