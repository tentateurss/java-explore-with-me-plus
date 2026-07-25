package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query("SELECT new ru.practicum.stats.dto.ViewStats(h.app, h.uri, COUNT(h.id)) "
            + "FROM Hit h "
            + "WHERE (COALESCE(:uris, NULL) IS NULL OR h.uri IN :uris) "
            + "AND h.timestamp BETWEEN :start AND :end "
            + "GROUP BY h.app, h.uri "
            + "ORDER BY COUNT(h.id) DESC")
    List<ViewStats> getHitsByDateLimits(LocalDateTime start, LocalDateTime end, @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.stats.dto.ViewStats(h.app, h.uri, COUNT(DISTINCT h.ip)) "
            + "FROM Hit h "
            + "WHERE (COALESCE(:uris, NULL) IS NULL OR h.uri IN :uris) "
            + "AND h.timestamp BETWEEN :start AND :end "
            + "GROUP BY h.app, h.uri "
            + "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStats> getHitsByDateAndUniqueIpLimits(LocalDateTime start, LocalDateTime end, @Param("uris") List<String> uris);
}