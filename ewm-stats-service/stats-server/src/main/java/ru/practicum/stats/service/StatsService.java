package ru.practicum.stats.service;

import ru.practicum.stats.dto.HitRequestDto;
import ru.practicum.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

//Взял из ветки stat_svc_controller
public interface StatsService {

    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);

    void saveStat(HitRequestDto hitRequestDto);
}
