package ru.practicum.stats.mapper;

import ru.practicum.stats.dto.HitRequestDto;
import ru.practicum.stats.model.Hit;

import java.time.LocalDateTime;

import static ru.practicum.stats.util.DateTimeFormatters.STANDARD;

public class HitMapper {

    public static Hit mapHitRequestDtoToHit(HitRequestDto hitRequestDto) {
        Hit newHit = new Hit();

        newHit.setApp(hitRequestDto.getApp());
        newHit.setIp(hitRequestDto.getIp());
        newHit.setUri(hitRequestDto.getUri());
        newHit.setTimestamp(
                LocalDateTime.parse(hitRequestDto.getTimestamp(), STANDARD)
        );

        return newHit;
    }
}