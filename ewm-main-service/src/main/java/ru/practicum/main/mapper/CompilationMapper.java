package ru.practicum.main.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main.dto.CompilationDto;
import ru.practicum.main.model.Compilation;

@UtilityClass
public class CompilationMapper {

    public CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto();
    }

}
