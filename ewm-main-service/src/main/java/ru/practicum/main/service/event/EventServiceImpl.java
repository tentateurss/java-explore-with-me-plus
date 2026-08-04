package ru.practicum.main.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.event.EventStatistics;
import ru.practicum.main.dto.event.NewEventDto;
import ru.practicum.main.dto.event.UpdateEventAdminRequest;
import ru.practicum.main.dto.event.UpdateEventUserRequest;
import ru.practicum.main.enums.EventState;
import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.EventMapper;
import ru.practicum.main.model.Category;
import ru.practicum.main.model.Event;
import ru.practicum.main.model.User;
import ru.practicum.main.repository.CategoryRepository;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<EventShortDto> getEvents(Long userId, Integer from, Integer size) {
        log.debug("Getting events for user id={}, from={}, size={}", userId, from, size);

        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);

        return events.stream()
                .map(event -> EventMapper.toShortDto(event, new EventStatistics(0, 0)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        log.debug("Creating event for user id={}", userId);

        User user = getUserById(userId);
        Category category = getCategoryById(newEventDto.getCategory());

        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Event date must be at least 2 hours from now");
        }

        Event event = EventMapper.toEntity(newEventDto, category, user);
        event = eventRepository.save(event);

        log.info("Event created: id={}, title={}", event.getId(), event.getTitle());

        return EventMapper.toFullDto(event, new EventStatistics(0, 0));
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        log.debug("Getting event id={} for user id={}", eventId, userId);

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " not found"));

        return EventMapper.toFullDto(event, new EventStatistics(0, 0));
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest request) {
        log.debug("Updating event id={} by user id={}", eventId, userId);

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " not found"));

        if (event.getState() != EventState.PENDING && event.getState() != EventState.CANCELED) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        if (request.getEventDate() != null &&
                request.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Event date must be at least 2 hours from now");
        }

        Category category = null;
        if (request.getCategory() != null) {
            category = getCategoryById(request.getCategory());
        }

        EventMapper.updateEntity(request, event, category);

        if (request.getStateAction() != null) {
            switch (request.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        event = eventRepository.save(event);

        log.info("Event updated by user: id={}, state={}", event.getId(), event.getState());

        return EventMapper.toFullDto(event, new EventStatistics(0, 0));
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest request) {
        log.debug("Updating event id={} by admin", eventId);

        Event event = getEventById(eventId);

        Category category = null;
        if (request.getCategory() != null) {
            category = getCategoryById(request.getCategory());
        }

        if (request.getEventDate() != null &&
                request.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ConflictException("Event date must be at least 1 hour from now");
        }

        EventMapper.updateEntity(event, request, category);

        if (request.getStateAction() != null) {
            switch (request.getStateAction()) {
                case PUBLISH_EVENT:
                    if (!"PENDING".equals(event.getState().name())) {
                        throw new ConflictException("Cannot publish event that is not in PENDING state");
                    }
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    if (event.getState() == EventState.PUBLISHED) {
                        throw new ConflictException("Cannot reject already published event");
                    }
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        event = eventRepository.save(event);

        log.info("Event updated by admin: id={}, state={}", event.getId(), event.getState());

        return EventMapper.toFullDto(event, new EventStatistics(0, 0));
    }

    @Override
    public List<EventFullDto> getEventsWithParameters(List<Long> users, List<String> states,
                                                      List<Long> categories, String rangeStart,
                                                      String rangeEnd, Integer from, Integer size) {
        log.debug("Getting events with parameters: users={}, states={}, categories={}", users, states, categories);

        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findEventsWithFilters(
                users, states, categories, rangeStart, rangeEnd, pageable);

        return events.stream()
                .map(event -> EventMapper.toFullDto(event, new EventStatistics(0, 0)))
                .collect(Collectors.toList());
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
    }

    private Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + categoryId + " not found"));
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " not found"));
    }
}