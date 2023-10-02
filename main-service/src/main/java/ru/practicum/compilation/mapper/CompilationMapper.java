package ru.practicum.compilation.mapper;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;

import java.util.List;

public interface CompilationMapper {
    static CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> events) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(events)
                .pinned(compilation.getPinned())
                .title(compilation.getTitle()).build();
    }

    static Compilation toNewCompilationDto(NewCompilationDto compilation, List<Event> events) {
        return Compilation.builder()
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(events).build();
    }
}