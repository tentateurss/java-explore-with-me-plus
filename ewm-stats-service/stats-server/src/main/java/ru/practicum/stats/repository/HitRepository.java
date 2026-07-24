package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query("SELECT new ViewStats(h.app, h.uri, COUNT(h.id)) "
            + "FROM Hit h "
            + "WHERE h.uri IN :uris "
            + "AND h.timestamp BETWEEN :start AND :end "
            + "GROUP BY h.app, h.uri")
    List<ViewStats> getHitsByDateLimits(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ViewStats(h.app, h.uri, COUNT(DISTINCT(h.ip))) "
            + "FROM Hit h "
            + "WHERE h.uri IN :uris "
            + "AND h.timestamp BETWEEN :start AND :end "
            + "GROUP BY h.app, h.uri")
    List<ViewStats> getHitsByDateAndUniqueIpLimits(LocalDateTime start, LocalDateTime end, List<String> uris);
}
