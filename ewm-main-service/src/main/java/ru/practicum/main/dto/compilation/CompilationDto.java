package ru.practicum.main.dto.compilation;

import lombok.*;
import ru.practicum.main.dto.event.EventShortDto;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    private long id;
    private String title;
    private Set<EventShortDto> events;
    private boolean pinned;
}
