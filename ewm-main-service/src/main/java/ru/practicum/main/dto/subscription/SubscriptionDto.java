package ru.practicum.main.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionDto {
    private Long id;
    private Long subscriberId;
    private UserShortDto author;
    private LocalDateTime createdAt;
}
