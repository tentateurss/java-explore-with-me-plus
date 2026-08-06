package ru.practicum.main.service.event;

import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.event.NewEventDto;
import ru.practicum.main.dto.event.UpdateEventAdminRequest;
import ru.practicum.main.dto.event.UpdateEventUserRequest;

import java.util.List;

public interface EventService {
    List<EventShortDto> getEvents(Long userId, Integer from, Integer size);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEvent(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest request);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest request);

    List<EventFullDto> getEventsWithParameters(List<Long> users, List<String> states,
                                               List<Long> categories, String rangeStart,
                                               String rangeEnd, Integer from, Integer size);
}