package ru.practicum.main.dto.compilation;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.validation.NullOrNotBlank;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationRequest {
    Set<Long> events;
    Boolean pinned;

    @NullOrNotBlank(message = "Название подборки не может быть пустым. " +
            "Если не требуется обновлять название подборки, не указывайте данное поле.")
    @Size(max = 50, message = "Название подборки не может превышать 50 символов.")
    String title;
}
