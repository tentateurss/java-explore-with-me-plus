package ru.practicum.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.main.enums.EventState;
import ru.practicum.main.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    boolean existsByCategoryId(Long categoryId);


    Optional<Event> findByIdAndState(Long id, EventState state);


    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);


    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    @Query("""
            SELECT e
            FROM Event e
            WHERE e.state = ru.practicum.main.enums.EventState.PUBLISHED
            AND (:categoriesEmpty = true OR e.category.id IN :categories)
            AND (cast(:rangeStart as timestamp) IS NULL OR e.eventDate >= :rangeStart)
            AND (cast(:rangeEnd as timestamp) IS NULL OR e.eventDate <= :rangeEnd)
            """)
    List<Event> findPublishedEvents(
            @Param("categories") List<Long> categories,
            @Param("categoriesEmpty") boolean categoriesEmpty,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable
    );

    @Query("""
            SELECT e FROM Event e
            WHERE (:usersEmpty = true OR e.initiator.id IN :users)
            AND (:statesEmpty = true OR e.state IN :states)
            AND (:categoriesEmpty = true OR e.category.id IN :categories)
            AND (COALESCE(:rangeStart, NULL) IS NULL OR e.eventDate >= :rangeStart)
            AND (COALESCE(:rangeEnd, NULL) IS NULL OR e.eventDate <= :rangeEnd)
            """)
    Page<Event> findEventsWithFilters(
            @Param("users") List<Long> users,
            @Param("usersEmpty") boolean usersEmpty,
            @Param("states") List<EventState> states,
            @Param("statesEmpty") boolean statesEmpty,
            @Param("categories") List<Long> categories,
            @Param("categoriesEmpty") boolean categoriesEmpty,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable
    );
}