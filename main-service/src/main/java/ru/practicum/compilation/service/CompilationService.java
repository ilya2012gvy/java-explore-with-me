package ru.practicum.compilation.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getCompilations(Boolean pinned, Pageable pageable);

    CompilationDto findById(long id);

    CompilationDto addCompilation(NewCompilationDto compilation);

    CompilationDto updateCompilation(long id, UpdateCompilationRequest updateCompilation);

    void delete(long id);
}