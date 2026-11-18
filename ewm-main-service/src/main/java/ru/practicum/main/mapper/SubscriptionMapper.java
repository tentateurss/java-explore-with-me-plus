package ru.practicum.main.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main.dto.subscription.SubscriberDto;
import ru.practicum.main.dto.subscription.SubscriptionDto;
import ru.practicum.main.dto.user.UserShortDto;
import ru.practicum.main.model.Subscription;

@UtilityClass
public class SubscriptionMapper {

    public static SubscriptionDto toSubscriptionDto(Subscription subscription,
                                                    UserShortDto author) {
        SubscriptionDto dto = new SubscriptionDto();
        dto.setId(subscription.getId());
        dto.setSubscriberId(subscription.getSubscriberId());
        dto.setAuthor(author);
        dto.setCreatedAt(subscription.getCreatedAt());
        return dto;
    }

    public static SubscriberDto toSubscriberDto(Subscription subscription,
                                                UserShortDto subscriber) {
        SubscriberDto dto = new SubscriberDto();
        dto.setId(subscription.getId());
        dto.setSubscriber(subscriber);
        dto.setAuthorId(subscription.getAuthorId());
        dto.setCreatedAt(subscription.getCreatedAt());
        return dto;
    }
}