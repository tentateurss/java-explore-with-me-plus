package ru.practicum.stats.mapper;

import ru.practicum.stats.dto.HitRequestDto;
import ru.practicum.stats.model.Hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HitMapper {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Hit mapHitRequestDtoToHit(HitRequestDto hitRequestDto) {
        Hit newHit = new Hit();

        newHit.setApp(hitRequestDto.getApp());
        newHit.setIp(hitRequestDto.getIp());
        newHit.setUri(hitRequestDto.getUri());
        newHit.setTimestamp(
                LocalDateTime.parse(hitRequestDto.getTimestamp(), FORMATTER)
        );

        return newHit;
    }
}