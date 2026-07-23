package ru.practicum.stats.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ViewStats {
    private String app;
    private String uri;
    private Long hits;
}
