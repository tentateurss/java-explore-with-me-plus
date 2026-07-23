package ru.practicum.stats.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ViewStats {
    private String app;
    private String uri;
    private Long hits;
}
