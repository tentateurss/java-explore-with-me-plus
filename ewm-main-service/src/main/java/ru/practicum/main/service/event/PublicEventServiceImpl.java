package ru.practicum.main.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.main.dto.EventDto;
import ru.practicum.main.dto.EventShortDto;
import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.EventMapper;
import ru.practicum.main.model.Event;
import ru.practicum.main.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    private final EventRepository eventRepository;

    @Override
    public List<EventShortDto> getAllEvents(String text, List<Integer> categories, Boolean paid,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                            String sort, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size, createSort(sort));

        Specification<Event> spec = createSpec(text, categories, paid, rangeStart, rangeEnd, onlyAvailable);

        return eventRepository.findAll(spec, pageRequest)
                .stream()
                .map(EventMapper::toEventShortDto)
                .toList();
    }

    @Override
    public EventDto getEventById(Long id) {
        Event event = eventRepository.findByIdAndRequestModerationTrue(id).orElseThrow(() ->
                new NotFoundException(String.format("Опубликованный event с id=%d не существует", id)));
        return EventMapper.toEventDto(event);
    }

    private Sort createSort(String sort) {
        Sort sorting = Sort.unsorted();
        if (sort != null && !sort.isBlank()) {
            if (sort.equals("EVENT_DATE")) {
                sorting = Sort.by("eventDate");
            } else if (sort.equals("VIEWS")) {
                sorting = Sort.by("views");
            } else {
                log.info("{} - был передан не существующий вид сортировки событий", sort);
                throw new BadRequestException(String.format("%s - был передан не существующий вид сортировки событий",
                        sort));
            }
        }
        return sorting;
    }

    private Specification<Event> createSpec(String text, List<Integer> categories, Boolean paid,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable) {
        Specification<Event> spec = Specification.where(null);

        if (text != null && !text.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                            cb.like(cb.lower(root.get("description")), "%" + text.toLowerCase() + "%")
                    )
            );
        }
        if (categories != null && !categories.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("category").get("id")
                    .in(categories));
        }
        if (paid != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("paid"),
                    paid));
        }
        if (rangeStart != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThan(root.get("eventDate"), rangeStart));
        } else {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThan(root.get("eventDate"), LocalDateTime.now()));
        }
        if (rangeEnd != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThan(root.get("eventDate"), rangeEnd));
        }
        spec = spec.and((root, query, cb) -> cb.equal(root.get("onlyAvailable"),
                onlyAvailable));

        return spec;
    }
}
