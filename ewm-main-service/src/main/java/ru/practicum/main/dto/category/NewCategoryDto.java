package ru.practicum.main.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewCategoryDto {
    @NotBlank(message = "Название не может быть пустым.")
    @Size(min = 1, max = 50, message = "Название не может быть больше 50 символов.")
    private String name;
}
