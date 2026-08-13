package ru.practicum.main.service.subscription;

import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.subscription.SubscriberDto;
import ru.practicum.main.dto.subscription.SubscriptionDto;

import java.time.LocalDateTime;
import java.util.List;

public interface SubscriptionService {

    void subscribe(Long subscriberId, Long authorId);

    void unsubscribe(Long subscriberId, Long authorId);

    List<SubscriptionDto> getSubscriptions(Long userId);

    List<SubscriberDto> getSubscribers(Long userId);

    List<EventFullDto> getEventsFromSubscriptions(Long userId, LocalDateTime start, LocalDateTime end, int from, int size);

}
