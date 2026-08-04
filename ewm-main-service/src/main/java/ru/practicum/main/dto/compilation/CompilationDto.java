package ru.practicum.main.dto.compilation;

import lombok.*;
import ru.practicum.main.dto.event.EventShortDto;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    private Long id;
    private String title;
    private Set<EventShortDto> events;
    private Boolean pinned;
}
