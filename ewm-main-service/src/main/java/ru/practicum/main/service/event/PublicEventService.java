package ru.practicum.main.service.event;

import ru.practicum.main.dto.EventDto;
import ru.practicum.main.dto.EventShortDto;

import java.time.LocalDateTime;
import java.util.List;

public interface PublicEventService {

    List<EventShortDto> getAllEvents(String text, List<Integer> categories, Boolean paid, LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                                     Integer size);

    EventDto getEventById(Long id);
}
