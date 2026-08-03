package ru.practicum.main.service.event;

import ru.practicum.main.dto.event.*;

import java.util.List;

//Это заглушка (хотя, вроде, так он и будет выглядеть)
public interface EventService {

    List<EventShortDto> getEvents(Long userId, Integer from, Integer size);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEvent(Long userId, Long eventId);

    //Перегруженный метод юзер/админ, посмотрим как будет у того, кто делает этот сервис
    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventFullDto> getEventsWithParameters(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer size);

}
