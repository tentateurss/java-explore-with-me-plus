package ru.practicum.main.service.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.event.EventSearchParams;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.subscription.SubscriberDto;
import ru.practicum.main.dto.subscription.SubscriptionDto;
import ru.practicum.main.dto.user.UserShortDto;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.SubscriptionMapper;
import ru.practicum.main.mapper.UserMapper;
import ru.practicum.main.model.Subscription;
import ru.practicum.main.repository.SubscriptionRepository;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.service.event.PublicEventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final PublicEventService eventService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void subscribe(Long subscriberId, Long authorId) {
        if (subscriberId.equals(authorId)) {
            throw new ConflictException("Нельзя подписаться на самого себя.");
        }
        if (!userRepository.existsById(subscriberId)) {
            throw new NotFoundException("Подписчик " + subscriberId + " не найден.");
        }
        if (!userRepository.existsById(authorId)) {
            throw new NotFoundException("Автор " + authorId + " не найден.");
        }
        if (subscriptionRepository.existsBySubscriberIdAndAuthorId(subscriberId, authorId)) {
            throw new ConflictException("Вы уже подписаны на этого пользователя.");
        }

        Subscription subscription = new Subscription();
        subscription.setSubscriberId(subscriberId);
        subscription.setAuthorId(authorId);
        subscription.setCreatedAt(LocalDateTime.now());

        subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public void unsubscribe(Long subscriberId, Long authorId) {
        if (!userRepository.existsById(subscriberId)) {
            throw new NotFoundException("Подписчик " + subscriberId + " не найден.");
        }
        if (!userRepository.existsById(authorId)) {
            throw new NotFoundException("Автор " + authorId + " не найден.");
        }
        if (!subscriptionRepository.existsBySubscriberIdAndAuthorId(subscriberId, authorId)) {
            throw new NotFoundException("Подписка не найдена. Вы не были подписаны на этого пользователя.");
        }

        subscriptionRepository.deleteBySubscriberIdAndAuthorId(subscriberId, authorId);
    }

    @Override
    public List<SubscriptionDto> getSubscriptions(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь " + userId + " не найден.");
        }
        List<Subscription> subscriptions = subscriptionRepository.findBySubscriberId(userId);
        Map<Long, UserShortDto> authors = getUserDtos(subscriptions.stream()
                .map(subscription -> subscription.getAuthorId())
                .toList());

        return subscriptions.stream()
                .map(subscription -> SubscriptionMapper.toSubscriptionDto(subscription,
                        authors.get(subscription.getAuthorId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<SubscriberDto> getSubscribers(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь " + userId + " не найден.");
        }
        List<Subscription> subscriptions = subscriptionRepository.findByAuthorId(userId);
        Map<Long, UserShortDto> subscribers = getUserDtos(subscriptions.stream()
                .map(subscription -> subscription.getSubscriberId())
                .toList());

        return subscriptions.stream()
                .map(subscription -> SubscriptionMapper.toSubscriberDto(subscription,
                        subscribers.get(subscription.getSubscriberId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getEventsFromSubscriptions(Long userId, EventSearchParams params) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь " + userId + " не найден.");
        }
        List<Subscription> subscriptions = subscriptionRepository.findBySubscriberId(userId);
        if (subscriptions.isEmpty()) {
            return List.of();
        }

        List<Long> authorIds = subscriptions.stream()
                .map(sub -> sub.getAuthorId())
                .collect(Collectors.toList());

        return eventService.getAllEvents(params)
                .stream()
                .filter(event -> authorIds.contains(event.getInitiator().getId()))
                .toList();
    }

    private Map<Long, UserShortDto> getUserDtos(List<Long> ids) {
        return userRepository.findAllById(ids)
                .stream()
                .collect(Collectors.toMap(author -> author.getId(), author -> UserMapper.toShortDto(author)));
    }
}
