package ru.practicum.stats.mapper;

import ru.practicum.stats.dto.HitRequestDto;
import ru.practicum.stats.model.Hit;

public class HitMapper {

    public static Hit mapHitRequestDtoToHit(HitRequestDto hitRequestDto) {
        Hit newHit = new Hit();

        newHit.setApp(hitRequestDto.getApp());
        newHit.setIp(hitRequestDto.getIp());
        newHit.setUri(hitRequestDto.getUri());
        newHit.setTimestamp(hitRequestDto.getTimestamp());

        return newHit;
    }

}
