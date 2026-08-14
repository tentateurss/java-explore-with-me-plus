package ru.practicum.main.service.event;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventSearchParams;
import ru.practicum.main.dto.event.EventShortDto;

import java.util.List;

public interface PublicEventService {

    List<EventShortDto> getAllEvents(EventSearchParams params);

    EventFullDto getEventById(Long id, HttpServletRequest request);
}