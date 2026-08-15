package ru.practicum.main.service.subscription;

import ru.practicum.main.dto.event.EventSearchParams;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.subscription.SubscriberDto;
import ru.practicum.main.dto.subscription.SubscriptionDto;

import java.util.List;

public interface SubscriptionService {

    void subscribe(Long subscriberId, Long authorId);

    void unsubscribe(Long subscriberId, Long authorId);

    List<SubscriptionDto> getSubscriptions(Long userId);

    List<SubscriberDto> getSubscribers(Long userId);

    List<EventShortDto> getEventsFromSubscriptions(Long userId, EventSearchParams params);

}
