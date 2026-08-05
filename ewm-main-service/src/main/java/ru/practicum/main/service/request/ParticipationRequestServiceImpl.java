package ru.practicum.main.service.request;

import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.main.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.main.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.main.dto.request.ParticipationRequestDto;
import ru.practicum.main.enums.EventState;
import ru.practicum.main.enums.RequestStatus;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.ParticipationRequestMapper;
import ru.practicum.main.model.Event;
import ru.practicum.main.model.ParticipationRequest;
import ru.practicum.main.model.User;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.repository.ParticipationRequestRepository;
import ru.practicum.main.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final UserRepository userRepository;
    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        checkUserExist(userId);
        return requestRepository.findAllByRequestorId(userId).stream()
                .map(ParticipationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        log.info("Creating request for user id={}, event id={}", userId, eventId);

        Event event = getEventById(eventId);

        if (requestRepository.existsByRequestorIdAndEventId(userId, eventId)) {
            throw new ConflictException("Реквест уже существует");
        }

        if (userId.equals(event.getInitiator().getId())) {
            throw new ConflictException("Инициатор не может запросить участие в собственном событии");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Это событие ещё не опубликовано");
        }

        long confirmedCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() != 0 && confirmedCount >= event.getParticipantLimit()) {
            throw new ConflictException("Достигнут лимит заявок на участие");
        }

        User user = getUserById(userId);
        ParticipationRequest request = new ParticipationRequest();
        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        request.setRequestor(user);

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        request = requestRepository.save(request);
        log.info("Request created: id={}, status={}", request.getId(), request.getStatus());

        return ParticipationRequestMapper.toDto(request);
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        log.info("Cancelling request id={} for user id={}", requestId, userId);

        ParticipationRequest request = requestRepository.findByIdAndRequestorId(requestId, userId);
        if (request == null) {
            throw new NotFoundException("Заявка не найдена или не принадлежит пользователю");
        }
        request.setStatus(RequestStatus.CANCELED);

        request = requestRepository.save(request);
        log.info("Request cancelled: id={}", request.getId());

        return ParticipationRequestMapper.toDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        checkUserExist(userId);
        checkEventExist(eventId);
        eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " пользователя с id=" + userId + " не найдено"));

        return requestRepository.findAllByEventId(eventId).stream()
                .map(ParticipationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest dto) {
        log.info("Changing request status for event id={}, user id={}", eventId, userId);

        User user = getUserById(userId);
        checkEventExist(eventId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " пользователя с id=" + userId + " не найдено"));

        if (!event.getInitiator().equals(user)) {
            throw new ValidationException("Этот пользователь не инициатор");
        }

        List<ParticipationRequest> requests = requestRepository.findAllById(dto.getRequestIds());

        if (requests.size() != dto.getRequestIds().size()) {
            throw new NotFoundException("Одна или несколько заявок не найдены");
        }

        List<ParticipationRequest> confirmed = new ArrayList<>();
        List<ParticipationRequest> rejected = new ArrayList<>();
        long confirmedCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        int limit = event.getParticipantLimit();

        for (ParticipationRequest request : requests) {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Статус заявки должен быть PENDING");
            }

            if (dto.getStatus() == RequestStatus.CONFIRMED) {
                if (limit == 0 || confirmedCount < limit) {
                    request.setStatus(RequestStatus.CONFIRMED);
                    confirmed.add(request);
                    confirmedCount++;
                } else {
                    request.setStatus(RequestStatus.REJECTED);
                    rejected.add(request);
                }
            } else {
                request.setStatus(RequestStatus.REJECTED);
                rejected.add(request);
            }
        }

        requestRepository.saveAll(confirmed);
        requestRepository.saveAll(rejected);

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setConfirmedRequests(confirmed.stream()
                .map(ParticipationRequestMapper::toDto)
                .collect(Collectors.toSet()));
        result.setRejectedRequests(rejected.stream()
                .map(ParticipationRequestMapper::toDto)
                .collect(Collectors.toSet()));

        log.info("Request status changed: confirmed={}, rejected={}", confirmed.size(), rejected.size());

        return result;
    }

    private void checkUserExist(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }

    private void checkEventExist(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие с id=" + eventId + " не найдено");
        }
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));
    }
}