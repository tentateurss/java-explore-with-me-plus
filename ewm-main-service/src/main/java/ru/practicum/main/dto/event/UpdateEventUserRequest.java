package ru.practicum.main.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.main.enums.EventStateUserAction;
import ru.practicum.main.validation.NullOrNotBlank;

import java.time.LocalDateTime;

@Data
public class UpdateEventUserRequest {
    @NullOrNotBlank(message = "Заголовок события не может быть пустым." +
            "Если не требуется обновлять данное поле, не указывайте его.")
    @Size(min = 3, max = 120, message = "Заголовок события должен быть в диапазоне [3-120] символов.")
    private String title;

    @NullOrNotBlank(message = "Краткое описание события не может быть пустым." +
            "Если не требуется обновлять данное поле, не указывайте его.")
    @Size(min = 20, max = 2000, message = "Краткое описание события должно быть в диапазоне [20-2000] символов.")
    private String annotation;

    @NullOrNotBlank(message = "Описание события не может быть пустым." +
            "Если не требуется обновлять данное поле, не указывайте его.")
    @Size(min = 20, max = 7000, message = "Описание события должно быть в диапазоне [20-7000] символов.")
    private String description;

    private Long category;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @Valid
    private LocationDto location;

    private Boolean paid;

    @PositiveOrZero(message = "Максимальное количество участников не может быть меньше 0.")
    private Integer participantLimit;

    private Boolean requestModeration;

    private EventStateUserAction stateAction;
}
