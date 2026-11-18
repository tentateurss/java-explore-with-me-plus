package ru.practicum.main.controller.publicapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.event.EventSearchParams;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.subscription.SubscriberDto;
import ru.practicum.main.dto.subscription.SubscriptionDto;
import ru.practicum.main.exception.ForbiddenException;
import ru.practicum.main.service.subscription.SubscriptionService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.main.constant.HeaderConstants.X_USER_ID;
import static ru.practicum.stats.dto.util.DateTimeFormatters.PATTERN;

@RestController
@RequestMapping(path = "/users/{userId}")
@RequiredArgsConstructor
@Slf4j
@Validated
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/subscriptions/{authorId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void subscribe(@PathVariable(name = "userId") Long userId,
                          @PathVariable(name = "authorId") Long authorId,
                          @RequestHeader(X_USER_ID) Long requesterId) {
        if (!userId.equals(requesterId)) {
            throw new ForbiddenException(String.format("Пользователь с id = %d не умеет доступа к данным пользователя "
                    + "с id = %d", userId, requesterId));
        }
        log.info("Подписки АПИ: подписка пользователя с id={} на пользователя с id={}", userId, authorId);
        subscriptionService.subscribe(userId, authorId);
    }

    @DeleteMapping("/subscriptions/{authorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(@PathVariable(name = "userId") Long userId,
                            @PathVariable(name = "authorId") Long authorId,
                            @RequestHeader(X_USER_ID) Long requesterId) {
        if (!userId.equals(requesterId)) {
            throw new ForbiddenException(String.format("Пользователь с id = %d не умеет доступа к данным пользователя "
                    + "с id = %d", userId, requesterId));
        }
        log.info("Подписки АПИ: отписка пользователя с id={} от пользователя с id={}", userId, authorId);
        subscriptionService.unsubscribe(userId, authorId);
    }

    @GetMapping("/subscriptions")
    public List<SubscriptionDto> getSubscriptions(@PathVariable(name = "userId") Long userId,
                                                  @RequestHeader(X_USER_ID) Long requesterId) {
        if (!userId.equals(requesterId)) {
            throw new ForbiddenException(String.format("Пользователь с id = %d не умеет доступа к данным пользователя "
                    + "с id = %d", userId, requesterId));
        }
        log.info("Подписки АПИ: получение списка подписок пользователя с id={}", userId);
        return subscriptionService.getSubscriptions(userId);
    }

    @GetMapping("/subscribers")
    public List<SubscriberDto> getSubscribers(@PathVariable(name = "userId") Long userId,
                                              @RequestHeader(X_USER_ID) Long requesterId) {
        if (!userId.equals(requesterId)) {
            throw new ForbiddenException(String.format("Пользователь с id = %d не умеет доступа к данным пользователя "
                    + "с id = %d", userId, requesterId));
        }
        log.info("Подписки АПИ: получение списка подписчиков пользователя с id={}", userId);
        return subscriptionService.getSubscribers(userId);
    }

    @GetMapping("/subscriptions/events")
    public List<EventShortDto> getEventsFromSubscriptions(@PathVariable(name = "userId") Long userId,
                                                          @RequestHeader(X_USER_ID) Long requesterId,
                                                          @RequestParam(required = false)
                                                         @DateTimeFormat(pattern = PATTERN) LocalDateTime start,
                                                          @RequestParam(required = false)
                                                         @DateTimeFormat(pattern = PATTERN) LocalDateTime end,
                                                          @RequestParam(defaultValue = "0") Integer from,
                                                          @RequestParam(defaultValue = "10") Integer size
    ) {
        if (!userId.equals(requesterId)) {
            throw new ForbiddenException(String.format("Пользователь с id = %d не умеет доступа к данным пользователя "
                    + "с id = %d", userId, requesterId));
        }
        log.info("Подписки АПИ: получение данных о ивентах пользователей из подписок пользователя с id={}", userId);

        EventSearchParams params = new EventSearchParams();
        params.setRangeStart(start);
        params.setRangeEnd(end);
        params.setFrom(from);
        params.setSize(size);

        return subscriptionService.getEventsFromSubscriptions(userId, params);
    }

}
