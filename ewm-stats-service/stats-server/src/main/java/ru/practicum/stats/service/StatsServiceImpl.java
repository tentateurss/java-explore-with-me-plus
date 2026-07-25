package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.stats.dto.HitRequestDto;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.mapper.HitMapper;
import ru.practicum.stats.model.Hit;
import ru.practicum.stats.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final HitRepository hitRepository;

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (uris == null || uris.isEmpty()) {
            // получаем все записи за период
            if (unique) {
                return hitRepository.getHitsByDateAndUniqueIpLimits(start, end, null);
            } else {
                return hitRepository.getHitsByDateLimits(start, end, null);
            }
        }

        // если uris заданы
        if (unique) {
            return hitRepository.getHitsByDateAndUniqueIpLimits(start, end, uris);
        } else {
            return hitRepository.getHitsByDateLimits(start, end, uris);
        }
    }

    @Override
    public void saveStat(HitRequestDto hitRequestDto) {
        Hit newHit = HitMapper.mapHitRequestDtoToHit(hitRequestDto);
        hitRepository.save(newHit);
        log.info("stat-server - Service: Hit c uri: {}, был успешно сохранен в базу данных", newHit.getUri());
    }

}
