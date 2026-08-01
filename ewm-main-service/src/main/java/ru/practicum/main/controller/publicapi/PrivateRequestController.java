package ru.practicum.main.controller.publicapi;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.dto.ParticipationRequestDto;
import ru.practicum.main.service.request.ParticipationRequestService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}")
public class PrivateRequestController {
    private final ParticipationRequestService requestService;

    //GET /users/{userId}/requests
    @GetMapping("/requests")
    public List<ParticipationRequestDto> getUserRequests(@PathVariable Long userId) {
        return requestService.getUserRequests(userId);
    }

    //POST /users/{userId}/requests
    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable Long userId,
                                                 @RequestParam Long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    //PATCH /users/{userId}/requests/{requestId}/cancel
    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId,
                                                 @PathVariable Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }

    //Не должен ли это эндпоинт быть в PrivateEventController, как в спецификации?
    //GET /users/{userId}/events/{eventId}/requests
    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(@PathVariable Long userId,
                              @PathVariable Long eventId) {
        return requestService.getEventRequests(userId, eventId);
    }

    //Не должен ли это эндпоинт быть в PrivateEventController, как в спецификации?
    //PATCH /users/{userId}/events/{eventId}/requests
    @PatchMapping("/events/{eventId}/requests")
    public EventRequestStatusUpdateResult changeRequestStatus(@PathVariable Long userId,
                                                              @PathVariable Long eventId,
                                                              @RequestBody EventRequestStatusUpdateRequest statusUpdateResult) {
        return requestService.changeRequestStatus(userId, eventId, statusUpdateResult);
    }

}
