package ru.practicum.main.dto.compilation;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    @NotBlank(message = "Название подборки не может быть пустым.")
    @Size(max = 50, message = "Название подборки не может быть больше 50 символов.")
    private String title;

    @NotEmpty(message = "Должен быть указан хотя бы один идентификатор события для создания подборки.")
    private Set<@NotNull(message = "Идентификатору события должно быть присвоено значение.") Long> events;

    private boolean pinned;
}
