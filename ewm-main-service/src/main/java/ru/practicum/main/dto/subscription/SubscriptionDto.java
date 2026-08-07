package ru.practicum.main.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionDto {

    private Long id;
    private Long subscriberId;
    private Long authorId;
    private String authorName;  // имя автора (подтягивается через другой сервис)
    private LocalDateTime createdAt;
}
