package ru.practicum.main.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.main.enums.EventState;
import ru.practicum.main.model.Event;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    boolean existsByCategoryId(Long categoryId);

    Optional<Event> findByIdAndState(Long id, EventState state);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    @Query(value = "SELECT * FROM events e " +
            "WHERE (:users IS NULL OR e.initiator_id IN (:users)) " +
            "AND (:states IS NULL OR e.state IN (:states)) " +
            "AND (:categories IS NULL OR e.category_id IN (:categories)) " +
            "AND (:rangeStart IS NULL OR e.event_date >= CAST(:rangeStart AS TIMESTAMP)) " +
            "AND (:rangeEnd IS NULL OR e.event_date <= CAST(:rangeEnd AS TIMESTAMP))",
            nativeQuery = true)
    List<Event> findEventsWithFilters(
            @Param("users") List<Long> users,
            @Param("states") List<String> states,
            @Param("categories") List<Long> categories,
            @Param("rangeStart") String rangeStart,
            @Param("rangeEnd") String rangeEnd,
            Pageable pageable
    );
}