package ru.practicum.main.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @NotBlank(message = "Заголовок события не может быть пустым.")
    @Size(min = 3, max = 120, message = "Заголовок события должен быть в диапазоне [3-120] символов.")
    private String title;

    @NotBlank(message = "Краткое описание события не может быть пустым.")
    @Size(min = 20, max = 2000, message = "Краткое описание события должно быть в диапазоне [20-2000] символов.")
    private String annotation;

    @NotBlank(message = "Описание события не может быть пустым.")
    @Size(min = 20, max = 7000, message = "Описание события должно быть в диапазоне [20-7000] символов.")
    private String description;

    @NotNull(message = "Идентификатору категории должно быть присвоено значение.")
    private Long category;

    @NotNull(message = "Дата события должна быть указана.")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @Valid
    @NotNull(message = "Координаты места запланированного события должны быть указаны.")
    private LocationDto location;

    private boolean paid;

    @PositiveOrZero(message = "Максимальное количество участников не может быть меньше 0.")
    private int participantLimit;

    private Boolean requestModeration;
}
