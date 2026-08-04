package ru.practicum.main.service.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.main.dto.compilation.CompilationDto;
import ru.practicum.main.dto.event.EventStatistics;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.CompilationMapper;
import ru.practicum.main.model.Compilation;
import ru.practicum.main.model.Event;
import ru.practicum.main.repository.CompilationRepository;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.stats.dto.util.DateTimeFormatters.STANDARD;

@Service
@Slf4j
@RequiredArgsConstructor
public class PublicCompilationServiceImpl implements PublicCompilationService {

    private final CompilationRepository compilationRepository;
    private final StatsClient statsClient;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<Compilation> compilations;
        if (pinned != null) {
            compilations = compilationRepository.findByPinned(pinned, pageRequest);
        } else {
            compilations = compilationRepository.findAll(pageRequest).getContent();
        }

        Set<Long> eventIds = compilations.stream()
                .flatMap(c -> c.getEvents().stream())
                .map(Event::getId)
                .collect(Collectors.toSet());

        Map<Long, EventStatistics> statsMap = getEventStatistics(eventIds);

        return compilations.stream()
                .map(compilation -> CompilationMapper.toDto(compilation, statsMap))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long id) {
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Подборки с id=%d не существует", id)));

        Set<Long> eventIds = compilation.getEvents().stream()
                .map(Event::getId)
                .collect(Collectors.toSet());

        Map<Long, EventStatistics> statsMap = getEventStatistics(eventIds);

        return CompilationMapper.toDto(compilation, statsMap);
    }

    private Map<Long, EventStatistics> getEventStatistics(Set<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return Map.of();
        }

        try {
            List<String> uris = eventIds.stream()
                    .map(id -> "/events/" + id)
                    .collect(Collectors.toList());

            LocalDateTime start = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
            LocalDateTime end = LocalDateTime.now().plusDays(1);

            List<ViewStats> viewStats = statsClient.getStats(
                    start.format(STANDARD),
                    end.format(STANDARD),
                    uris.toArray(new String[0]),
                    false
            ).getBody();

            if (viewStats == null) {
                return Map.of();
            }

            Map<Long, Long> viewsMap = viewStats.stream()
                    .collect(Collectors.toMap(
                            stat -> extractEventIdFromUri(stat.getUri()),
                            ViewStats::getHits
                    ));

            return eventIds.stream()
                    .collect(Collectors.toMap(
                            id -> id,
                            id -> new EventStatistics(0L, viewsMap.getOrDefault(id, 0L))
                    ));

        } catch (Exception e) {
            log.error("Ошибка при получении статистики из сервиса статистики", e);
            return eventIds.stream()
                    .collect(Collectors.toMap(
                            id -> id,
                            id -> new EventStatistics(0L, 0L)
                    ));
        }
    }

    private Long extractEventIdFromUri(String uri) {
        String[] parts = uri.split("/");
        return Long.parseLong(parts[parts.length - 1]);
    }
}