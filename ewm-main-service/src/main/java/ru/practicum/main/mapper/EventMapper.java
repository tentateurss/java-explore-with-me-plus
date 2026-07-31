package ru.practicum.main.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main.dto.EventDto;
import ru.practicum.main.dto.EventShortDto;
import ru.practicum.main.model.Event;

@UtilityClass
public class EventMapper {

    public EventShortDto toEventShortDto(Event event) {
        return new EventShortDto();
    }

    public EventDto toEventDto(Event event) {
        return new EventDto();
    }
}
