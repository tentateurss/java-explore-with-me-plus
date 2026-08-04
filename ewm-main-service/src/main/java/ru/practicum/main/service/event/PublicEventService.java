package ru.practicum.main.service.event;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventShortDto;

import java.time.LocalDateTime;
import java.util.List;

public interface PublicEventService {

    List<EventShortDto> getAllEvents(String text, List<Long> categories, Boolean paid,
                                     LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                     Boolean onlyAvailable, String sort, Integer from,
                                     Integer size, HttpServletRequest request);

    EventFullDto getEventById(Long id, HttpServletRequest request);
}