package ru.practicum.main.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriberDto {
    private Long id;
    private UserShortDto subscriber;
    private Long authorId;
    private LocalDateTime createdAt;
}
