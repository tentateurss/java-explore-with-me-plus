package ru.practicum.main.controller.publicapi;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventSearchParams;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.exception.AuthorizationException;
import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.service.event.PublicEventService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.stats.dto.util.DateTimeFormatters.PATTERN;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicEventController {

    private static final String X_USER_ID = "X-User-Id";

    private final PublicEventService publicEventService;
    private final SubscriptionService subscriptionService;

    @GetMapping
    public List<EventShortDto> getAllEvents(
            @RequestHeader(value = X_USER_ID, required = false) Long userId,
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "false") Boolean subscriptions,
            HttpServletRequest request) {

        log.info("Публичный АПИ: получение информации о событиях text={}, categories={}, paid={}, rangeStart={}, " +
                        "rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("rangeStart must be before rangeEnd");
        }

        if (sort != null && !sort.isEmpty() && !"EVENT_DATE".equals(sort) && !"VIEWS".equals(sort)) {
            throw new BadRequestException("sort must be EVENT_DATE or VIEWS");
        }

        EventSearchParams params = new EventSearchParams(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, request);

        return subscriptions ? getEventsBySubscription(params, userId) :
                publicEventService.getAllEvents(params);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(
            @PathVariable Long id,
            HttpServletRequest request) {
        log.info("Публичный АПИ: получение информации о событии id={}", id);
        return publicEventService.getEventById(id, request);
    }

    private List<EventShortDto> getEventsBySubscription(EventSearchParams params, Long userId) {
        if (userId == null) {
            throw new AuthorizationException("Для получения событий по подпискам требуется предоставить id пользователя");
        }
        return subscriptionService.getEventsFromSubscriptions(userId, params);
    }
}