package ru.practicum.main.dto;

import lombok.Data;

import java.time.LocalDateTime;

//ЗАГЛУШКА
@Data
public class EventShortDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    private LocalDateTime eventDate;
    private UserDto initiator;
    private Boolean paid;
    private String title;
    private Long views;
}
