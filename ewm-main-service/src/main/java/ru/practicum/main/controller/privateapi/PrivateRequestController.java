package ru.practicum.main.controller.privateapi;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.main.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.main.dto.request.ParticipationRequestDto;
import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.service.request.ParticipationRequestService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}")
public class PrivateRequestController {
    private final ParticipationRequestService requestService;

    //GET /users/{userId}/requests
    @GetMapping("/requests")
    public List<ParticipationRequestDto> getUserRequests(@PathVariable Long userId) {
        log.info("main-server - PrivateRequestController: Получаем все запросы пользователя userId={}",
                userId);
        return requestService.getUserRequests(userId);
    }

    //POST /users/{userId}/requests
    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable Long userId,
                                                 @RequestParam(required = false) Long eventId) {
        if (eventId == null) {
            throw new BadRequestException("eventId parameter is required");
        }
        return requestService.createRequest(userId, eventId);
    }

    //PATCH /users/{userId}/requests/{requestId}/cancel
    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId,
                                                 @PathVariable Long requestId) {
        log.info("main-server - PrivateRequestController: Отменяем запрос requestId={} пользователя userId={}",
                requestId, userId);
        return requestService.cancelRequest(userId, requestId);
    }

    //GET /users/{userId}/events/{eventId}/requests
    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(@PathVariable Long userId,
                              @PathVariable Long eventId) {
        log.info("main-server - PrivateRequestController: Получаем запросы по событию eventId={} пользователя userId={}",
                eventId, userId);
        return requestService.getEventRequests(userId, eventId);
    }

    //PATCH /users/{userId}/events/{eventId}/requests
    @PatchMapping("/events/{eventId}/requests")
    public EventRequestStatusUpdateResult changeRequestStatus(@PathVariable Long userId,
                                                              @PathVariable Long eventId,
                                                              @Valid @RequestBody EventRequestStatusUpdateRequest statusUpdateResult) {
        log.info("main-server - PrivateRequestController: Меняем статус запросов события eventId={} пользователя userId={}",
                eventId, userId);
        return requestService.changeRequestStatus(userId, eventId, statusUpdateResult);
    }

}
