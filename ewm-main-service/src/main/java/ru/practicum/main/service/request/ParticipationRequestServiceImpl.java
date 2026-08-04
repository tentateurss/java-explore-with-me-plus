package ru.practicum.main.service.request;

import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.main.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.main.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.main.dto.request.ParticipationRequestDto;
import ru.practicum.main.enums.EventState;
import ru.practicum.main.enums.RequestStatus;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.model.Event;
import ru.practicum.main.model.ParticipationRequest;
import ru.practicum.main.model.User;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.repository.ParticipationRequestRepository;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.mapper.ParticipationRequestMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final UserRepository userRepository;
    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;

    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        checkUserExist(userId);
        return requestRepository.findAllByRequestorId(userId).stream()
                .map(ParticipationRequestMapper::toDto).collect(Collectors.toList());
    }

    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
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
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <=
                requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED)) {
            throw new ConflictException("Достигнут лимит заявок на участие");
        }

        User user = getUserById(userId);
        ParticipationRequest request = new ParticipationRequest();
        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        request.setRequestor(user);

        if (event.getRequestModeration() && event.getParticipantLimit() != 0) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        return ParticipationRequestMapper.toDto(requestRepository.save(request));
    }

    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findByIdAndRequestorId(requestId, userId);
        if (request == null) {
            throw new NotFoundException("Заявка не найдена или не принадлежит пользователю");
        }
        request.setStatus(RequestStatus.CANCELED);
        return ParticipationRequestMapper.toDto(requestRepository.save(request));
    }

    public List<ParticipationRequestDto>  getEventRequests(Long userId, Long eventId) {
        checkUserExist(userId);
        checkEventExist(eventId);
        eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException("Событие с id=" + eventId + " пользователя с id=" + userId +  " не найдено"));
        return requestRepository.findAllByEventId(eventId).stream()
                .map(ParticipationRequestMapper::toDto).collect(Collectors.toList());
    }

    public EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest dto) {
        User user = getUserById(userId);
        checkEventExist(eventId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException("Событие с id=" + eventId + " пользователя с id=" + userId +  " не найдено"));
        if (!event.getInitiator().equals(user)) {
           throw new ValidationException("Этот пользователь не инициатор");
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <=
                requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED)) {
            throw new ConflictException("Достигнут лимит заявок на участие");
        }

        //Вот это нужно доделать

        List<ParticipationRequest> confirmed = new ArrayList<>();
        List<ParticipationRequest> rejected = new ArrayList<>();
        long confirmedCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        int limit = event.getParticipantLimit();

        for (Long requestId : dto.getRequestIds()) {
            ParticipationRequest request = requestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException("Заявка не найдена"));

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

        return result;
    }

    private void checkUserExist(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }

    private void checkEventExist(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие с id=" + eventId + " не найдено");
        }
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id=" + eventId + " не найдено"));
    }
}
