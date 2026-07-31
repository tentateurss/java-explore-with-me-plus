package ru.practicum.main.controller.publicapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.EventDto;
import ru.practicum.main.dto.EventShortDto;
import ru.practicum.main.service.event.PublicEventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicEventController {

    private final PublicEventService publicEventService;

    @GetMapping
    public List<EventShortDto> getAllEvents(@RequestParam(defaultValue = "") String text,
                                            @RequestParam(defaultValue = "") List<Integer> categories,
                                            @RequestParam(required = false) Boolean paid,
                                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd "
                                                    + "HH:mm:ss") LocalDateTime rangeStart,
                                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd "
                                                    + "HH:mm:ss") LocalDateTime rangeEnd,
                                            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                            @RequestParam(defaultValue = "VIEWS") String sort,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Публичный АПИ: получение информации о событиях text={}, categories={}, paid={}, rangeStart={}, " +
                        "rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}", text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sort, from, size);
        return publicEventService.getAllEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from,
                size);
    }

    @GetMapping("/{id}")
    public EventDto getEventById(@PathVariable Long id) {
        log.info("Публичный АПИ: получение информации о событии id={}", id);
        return publicEventService.getEventById(id);
    }
}
