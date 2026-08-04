package ru.practicum.main.mapper;

import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;
import ru.practicum.main.dto.event.*;
import ru.practicum.main.enums.EventState;
import ru.practicum.main.model.Category;
import ru.practicum.main.model.Event;
import ru.practicum.main.model.Location;
import ru.practicum.main.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class EventMapper {

    public EventFullDto toFullDto(Event event, EventStatistics stats) {
        EventFullDto dto = new EventFullDto();

        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setAnnotation(event.getAnnotation());
        dto.setDescription(event.getDescription());
        dto.setCategory(CategoryMapper.toDto(event.getCategory()));
        dto.setPaid(event.getPaid());
        dto.setState(event.getState());

        if (event.getLocation() != null) {
            dto.setLocation(new LocationDto(event.getLocation().getLat(), event.getLocation().getLon()));
        }

        dto.setEventDate(event.getEventDate());
        dto.setCreatedOn(event.getCreatedOn());
        dto.setPublishedOn(event.getPublishedOn());
        dto.setInitiator(UserMapper.toShortDto(event.getInitiator()));
        dto.setParticipantLimit(event.getParticipantLimit());
        dto.setConfirmedRequests(stats != null ? stats.getConfirmedRequests() : 0L);
        dto.setViews(stats != null ? stats.getViews() : 0L);
        dto.setRequestModeration(event.getRequestModeration());

        return dto;
    }

    public EventShortDto toShortDto(Event event, EventStatistics stats) {
        EventShortDto dto = new EventShortDto();

        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setAnnotation(event.getAnnotation());
        dto.setCategory(CategoryMapper.toDto(event.getCategory()));
        dto.setPaid(event.getPaid());
        dto.setEventDate(event.getEventDate());
        dto.setInitiator(UserMapper.toShortDto(event.getInitiator()));
        dto.setConfirmedRequests(stats != null ? stats.getConfirmedRequests() : 0L);
        dto.setViews(stats != null ? stats.getViews() : 0L);

        return dto;
    }

    public Event toEntity(NewEventDto dto, Category category, User initiator) {
        Event event = new Event();

        event.setTitle(dto.getTitle().trim());
        event.setAnnotation(dto.getAnnotation().trim());
        event.setDescription(dto.getDescription());
        event.setCategory(category);
        event.setEventDate(dto.getEventDate());

        if (dto.getLocation() != null) {
            event.setLocation(new Location(dto.getLocation().getLat(), dto.getLocation().getLon()));
        }

        event.setPaid(dto.isPaid());
        event.setParticipantLimit(dto.getParticipantLimit());
        event.setRequestModeration(dto.getRequestModeration() == null || dto.getRequestModeration());
        event.setCreatedOn(LocalDateTime.now());
        event.setInitiator(initiator);
        event.setState(EventState.PENDING);

        return event;
    }

    public void updateEntity(Event event, UpdateEventAdminRequest dto, @Nullable Category category) {
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle().trim());
        }

        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation().trim());
        }

        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription().trim());
        }

        if (category != null) {
            event.setCategory(category);
        }

        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }

        if (dto.getLocation() != null) {
            event.setLocation(new Location(dto.getLocation().getLat(), dto.getLocation().getLon()));
        }

        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }

        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }

        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }

        if (dto.getStateAction() != null) {
            event.setState(dto.getStateAction().getAssociatedState());
        }
    }

    public void updateEntity(UpdateEventUserRequest dto, Event event, @Nullable Category category) {
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle().trim());
        }

        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation().trim());
        }

        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription().trim());
        }

        if (category != null) {
            event.setCategory(category);
        }

        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }

        if (dto.getLocation() != null) {
            event.setLocation(new Location(dto.getLocation().getLat(), dto.getLocation().getLon()));
        }

        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }

        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }

        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }

        if (dto.getStateAction() != null) {
            event.setState(dto.getStateAction().getAssociatedState());
        }
    }
}