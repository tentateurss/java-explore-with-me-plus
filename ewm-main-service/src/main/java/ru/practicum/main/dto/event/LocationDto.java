package ru.practicum.main.dto.event;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    @NotNull(message = "Широта должна быть указана.")
    private Double lat;
    @NotNull(message = "Долгота должна быть указана.")
    private Double lon;
}
