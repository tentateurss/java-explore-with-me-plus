package ru.practicum.stats.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.HitRequestDto;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.service.StatsService;
import ru.practicum.stats.dto.util.DateTimeFormatters;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStats> getStats(
            @RequestParam(required = false)
            @DateTimeFormat(pattern = DateTimeFormatters.PATTERN)
            LocalDateTime start,

            @RequestParam(required = false)
            @DateTimeFormat(pattern = DateTimeFormatters.PATTERN)
            LocalDateTime end,

            @RequestParam(required = false)
            List<String> uris,

            @RequestParam(defaultValue = "false")
            boolean unique) {

        log.info("stat-server - Controller: Получаем объекты статистики по параметрам: start {}, end {}, uris {}, unique {}",
                start, end, uris, unique);

        if (start == null) {
            throw new IllegalArgumentException("Start date must be provided");
        }

        if (end == null) {
            throw new IllegalArgumentException("End date must be provided");
        }

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        return statsService.getStats(start, end, uris, unique);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveStat(@Valid @RequestBody HitRequestDto hitRequestDto) {
        log.info("stat-server - Controller: Создаём объект статистики");
        statsService.saveStat(hitRequestDto);
    }
}