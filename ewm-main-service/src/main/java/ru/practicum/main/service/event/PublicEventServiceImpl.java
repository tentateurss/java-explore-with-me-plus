package ru.practicum.main.service.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.event.EventStatistics;
import ru.practicum.main.enums.EventState;
import ru.practicum.main.enums.RequestStatus;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.EventMapper;
import ru.practicum.main.model.Event;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.repository.ParticipationRequestRepository;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.stats.dto.util.DateTimeFormatters.STANDARD;

@Service
@Slf4j
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    private final EventRepository eventRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final StatsClient statsClient;

    @Override
    public List<EventShortDto> getAllEvents(String text, List<Long> categories, Boolean paid,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                            Boolean onlyAvailable, String sort,
                                            Integer from, Integer size,
                                            HttpServletRequest request) {

        log.info("PublicEventService: получение событий с параметрами text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);


        saveHit(request);

        if (from == null) {
            from = 0;
        }

        if (size == null) {
            size = 10;
        }

        if (rangeStart == null && rangeEnd == null) {
            rangeStart = LocalDateTime.now();
        }


        if (categories != null && categories.isEmpty()) {
            categories = null;
        }


        PageRequest pageRequest;

        if ("EVENT_DATE".equals(sort)) {
            pageRequest = PageRequest.of(
                    from / size,
                    size,
                    Sort.by("eventDate").ascending()
            );
        } else {
            pageRequest = PageRequest.of(
                    from / size,
                    size
            );
        }


        List<Event> events = eventRepository.findPublishedEvents(
                categories == null ? List.of(-1L) : categories,
                categories == null,
                rangeStart,
                rangeEnd,
                pageRequest
        );

        if (text != null && !text.isBlank()) {
            String searchText = text.toLowerCase();
            events = events.stream()
                    .filter(event ->
                            event.getAnnotation().toLowerCase().contains(searchText) ||
                                    event.getDescription().toLowerCase().contains(searchText)
                    )
                    .collect(Collectors.toList());
        }

        if (paid != null) {
            events = events.stream()
                    .filter(event -> event.getPaid().equals(paid))
                    .collect(Collectors.toList());
        }

        if (onlyAvailable != null && onlyAvailable) {
            events = events.stream()
                    .filter(event -> {
                        if (event.getParticipantLimit() == 0) return true;
                        long confirmed = participationRequestRepository
                                .countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
                        return confirmed < event.getParticipantLimit();
                    })
                    .collect(Collectors.toList());
        }

        Map<Long, EventStatistics> statsMap = getEventStatistics(events);

        List<EventShortDto> result = events.stream()
                .map(event -> EventMapper.toShortDto(event, statsMap.getOrDefault(event.getId(), new EventStatistics(0, 0))))
                .collect(Collectors.toList());

        if ("VIEWS".equals(sort)) {
            result.sort((e1, e2) -> Long.compare(e2.getViews(), e1.getViews()));
        }

        return result;
    }

    @Override
    public EventFullDto getEventById(Long id, HttpServletRequest request) {
        log.info("PublicEventService: получение события по id={}", id);

        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(String.format("Опубликованное событие с id=%d не найдено", id)));

        saveHit(request);

        Map<Long, EventStatistics> statsMap = getEventStatistics(List.of(event));
        EventStatistics stats = statsMap.getOrDefault(id, new EventStatistics(0, 0));

        return EventMapper.toFullDto(event, stats);
    }

    private Map<Long, EventStatistics> getEventStatistics(List<Event> events) {
        if (events == null || events.isEmpty()) {
            return Map.of();
        }

        try {
            Map<Long, Long> confirmedRequestsMap = events.stream()
                    .collect(Collectors.toMap(
                            Event::getId,
                            event -> participationRequestRepository
                                    .countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED)
                    ));

            List<String> uris = events.stream()
                    .map(event -> "/events/" + event.getId())
                    .collect(Collectors.toList());

            log.info("=== GETTING STATS FOR URIS: {}", uris);

            LocalDateTime start = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
            LocalDateTime end = LocalDateTime.now().plusDays(1);

            List<ViewStats> viewStats = statsClient.getStats(
                    start.format(STANDARD),
                    end.format(STANDARD),
                    uris.toArray(new String[0]),
                    true
            ).getBody();

            log.info("=== VIEW STATS RESPONSE: {}", viewStats);

            Map<Long, Long> viewsMap = Map.of();
            if (viewStats != null && !viewStats.isEmpty()) {
                viewsMap = viewStats.stream()
                        .collect(Collectors.toMap(
                                stat -> extractEventIdFromUri(stat.getUri()),
                                ViewStats::getHits
                        ));
            }

            log.info("=== VIEWS MAP: {}", viewsMap);

            Map<Long, Long> finalViewsMap = viewsMap;
            return events.stream()
                    .collect(Collectors.toMap(
                            Event::getId,
                            event -> new EventStatistics(
                                    confirmedRequestsMap.getOrDefault(event.getId(), 0L),
                                    finalViewsMap.getOrDefault(event.getId(), 0L)
                            )
                    ));

        } catch (Exception e) {
            log.error("Ошибка при получении статистики из сервиса статистики", e);
            return events.stream()
                    .collect(Collectors.toMap(
                            Event::getId,
                            event -> new EventStatistics(0L, 0L)
                    ));
        }
    }

    private Long extractEventIdFromUri(String uri) {
        String[] parts = uri.split("/");
        return Long.parseLong(parts[parts.length - 1]);
    }

    private void saveHit(HttpServletRequest request) {
        try {
            String uri = request.getRequestURI();
            String ip = request.getRemoteAddr();
            log.info("=== SAVING HIT: uri={}, ip={}", uri, ip);

            statsClient.saveHit(
                    "ewm-main-service",
                    uri,
                    ip,
                    LocalDateTime.now()
            );
            log.info("=== HIT SAVED SUCCESSFULLY");
        } catch (Exception e) {
            log.error("=== FAILED TO SAVE HIT: {}", e.getMessage(), e);
        }
    }
}