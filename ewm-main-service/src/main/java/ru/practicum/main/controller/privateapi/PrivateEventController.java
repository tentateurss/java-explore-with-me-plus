package ru.practicum.main.controller.privateapi;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.event.NewEventDto;
import ru.practicum.main.dto.event.UpdateEventUserRequest;
import ru.practicum.main.service.event.EventService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {
    private final EventService eventService;

    //GET /users/{userId}/events
    @GetMapping
    public List<EventShortDto> getEvents(@PathVariable Long userId,
                                             @RequestParam(required = false, defaultValue = "0") Integer from,
                                             @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("main-server - PrivateEventController: Получаем события пользователя userId={} по параметрам from={}, size ={}",
                userId, from, size);
        return eventService.getEvents(userId, from, size);
    }

    //POST /users/{userId}/events
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        log.info("main-server - PrivateEventController: Создаём событие пользователя userId={}", userId);
        return eventService.createEvent(userId, newEventDto);
    }

    //GET /users/{userId}/events/{eventId}
    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable Long userId,
                                 @PathVariable Long eventId) {
        log.info("main-server - PrivateEventController: Получаем событие eventId={} пользователя userId={}",
                eventId, userId);
        return eventService.getEvent(userId, eventId);
    }

    //PATCH /users/{userId}/events/{eventId}
    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("main-server - PrivateEventController: Обновляем событие eventId={} пользователя userId={}",
                eventId, userId);
        return eventService.updateEvent(userId, eventId, updateEventUserRequest);
    }

}
