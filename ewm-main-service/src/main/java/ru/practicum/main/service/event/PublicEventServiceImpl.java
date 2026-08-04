package ru.practicum.main.service.event;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.event.EventStatistics;
import ru.practicum.main.enums.EventState;
import ru.practicum.main.enums.RequestStatus;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.EventMapper;
import ru.practicum.main.model.Event;
import ru.practicum.main.model.ParticipationRequest;
import ru.practicum.main.repository.EventRepository;
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
    private final StatsClient statsClient;

    @Override
    public List<EventShortDto> getAllEvents(String text, List<Long> categories, Boolean paid,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                            String sort, Integer from, Integer size, HttpServletRequest request) {

        log.info("PublicEventService: получение событий с параметрами text={}, categories={}, paid={}, rangeStart={}, " +
                        "rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        saveHit(request);

        if (rangeStart == null && rangeEnd == null) {
            rangeStart = LocalDateTime.now();
        }

        Specification<Event> spec = createSpec(text, categories, paid, rangeStart, rangeEnd, onlyAvailable);

        PageRequest pageRequest;
        if ("EVENT_DATE".equals(sort)) {
            pageRequest = PageRequest.of(from / size, size, Sort.by("eventDate").ascending());
        } else {
            pageRequest = PageRequest.of(from / size, size);
        }

        List<Event> events = eventRepository.findAll(spec, pageRequest).getContent();

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

    private Specification<Event> createSpec(String text, List<Long> categories, Boolean paid,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable) {

        Specification<Event> spec = Specification.where(
                (root, query, cb) -> cb.equal(root.get("state"), EventState.PUBLISHED)
        );

        if (text != null && !text.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                            cb.like(cb.lower(root.get("description")), "%" + text.toLowerCase() + "%")
                    )
            );
        }

        if (categories != null && !categories.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("category").get("id").in(categories));
        }

        if (paid != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("paid"), paid));
        }

        if (rangeStart != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }
        if (rangeEnd != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        if (onlyAvailable != null && onlyAvailable) {
            spec = spec.and((root, query, cb) -> {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<ParticipationRequest> requestRoot = subquery.from(ParticipationRequest.class);
                subquery.select(cb.count(requestRoot))
                        .where(
                                cb.equal(requestRoot.get("event"), root),
                                cb.equal(requestRoot.get("status"), RequestStatus.CONFIRMED)
                        );
                return cb.or(
                        cb.equal(root.get("participantLimit"), 0),
                        cb.lessThan(subquery, root.get("participantLimit"))
                );
            });
        }

        return spec;
    }

    private Map<Long, EventStatistics> getEventStatistics(List<Event> events) {
        if (events == null || events.isEmpty()) {
            return Map.of();
        }

        try {
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

            if (viewStats == null || viewStats.isEmpty()) {
                log.warn("=== STATS RESPONSE IS EMPTY, returning default values");
                return events.stream()
                        .collect(Collectors.toMap(
                                Event::getId,
                                event -> new EventStatistics(0L, 0L)
                        ));
            }

            Map<Long, Long> viewsMap = viewStats.stream()
                    .collect(Collectors.toMap(
                            stat -> extractEventIdFromUri(stat.getUri()),
                            ViewStats::getHits
                    ));

            log.info("=== VIEWS MAP: {}", viewsMap);

            return events.stream()
                    .collect(Collectors.toMap(
                            Event::getId,
                            event -> new EventStatistics(0L, viewsMap.getOrDefault(event.getId(), 0L))
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