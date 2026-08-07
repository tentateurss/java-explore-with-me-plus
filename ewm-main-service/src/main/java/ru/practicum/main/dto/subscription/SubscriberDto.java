package ru.practicum.main.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriberDto {

    private Long id;
    private Long subscriberId;
    private String subscriberName;  // имя подписчика
    private Long authorId;
    private LocalDateTime createdAt;
}
