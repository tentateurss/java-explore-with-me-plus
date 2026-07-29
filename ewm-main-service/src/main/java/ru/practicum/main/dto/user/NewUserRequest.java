package ru.practicum.main.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewUserRequest {
    @NotBlank(message = "Имя пользователя не может быть пустым.")
    @Size(min = 2, max = 250, message = "Имя пользователя должно быть в пределах [2-250] символов.")
    private String name;

    @NotBlank(message = "Поле email не может быть пустым.")
    @Email(message = "Неверный формат email.")
    @Size(min = 6, max = 254, message = "Поле email должно быть в пределах [6-254] символов.")
    private String email;
}
