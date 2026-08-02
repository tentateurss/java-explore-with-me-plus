package ru.practicum.main.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.enums.RequestStatus;
import ru.practicum.main.validation.ValidUpdateRequestStatus;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateRequest {
    @NotEmpty(message = "Должен быть указан хотя бы один идентификатор запроса на участие в событии.")
    private Set<@NotNull(message = "Идентификатору запроса на участие в событии должно быть присвоено значение.")
            Long> requestIds;

    @ValidUpdateRequestStatus
    private RequestStatus status;
}
