package ru.practicum.main.service.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.subscription.SubscriberDto;
import ru.practicum.main.dto.subscription.SubscriptionDto;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.SubscriptionMapper;
import ru.practicum.main.model.Subscription;
import ru.practicum.main.repository.SubscriptionRepository;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService{

    private final SubscriptionRepository subscriptionRepository;
    private final EventService eventService;
    private final UserRepository userRepository;
    private final SubscriptionMapper subscriptionMapper;

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

        Subscription subscription = Subscription.builder() //Не знаю через билдер будем или через конструктор, люблю билдеры
                .subscriberId(subscriberId)
                .authorId(authorId)
                .build();

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
    @Transactional(readOnly = true)
    public List<SubscriptionDto> getSubscriptions(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь " + userId + " не найден.");
        }
        List<Subscription> subscriptions = subscriptionRepository.findBySubscriberId(userId);

        return subscriptions.stream()
                .map(subscriptionMapper::toDto) //Перепроверить название метода
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriberDto> getSubscribers(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь " + userId + " не найден.");
        }
        List<Subscription> subscribers = subscriptionRepository.findByAuthorId(userId);

        return subscribers.stream()
                .map(subscriptionMapper::toDto) //Перепроверить название метода
                .collect(Collectors.toList());
    }

    @Override
    public List<EventFullDto> getEventsFromSubscriptions(Long userId, LocalDateTime start, LocalDateTime end, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь " + userId + " не найден.");
        }
        List<Subscription> subscriptions = subscriptionRepository.findBySubscriberId(userId);
        if (subscriptions.isEmpty()) {
            return List.of();
        }

        List<Long> authorIds = subscriptions.stream()
                .map(sub -> sub.getAuthorId()) //Не забыть свериться по названию поля
                .collect(Collectors.toList());

        //В этот метод мы приняли LocalDateTime, а передать надо String
        String startStr = start != null ? start.toString() : null;
        String endStr = end != null ? end.toString() : null;

        return eventService.getEventsWithParameters(
                authorIds,
                List.of("PUBLISHED"), //Полагаю, нет смысла искать другие типы?
                null,
                startStr,
                endStr,
                from,
                size
        );
    }
}
