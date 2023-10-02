package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
public class CompilationAdminController {
    private final CompilationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@Valid @RequestBody NewCompilationDto compilation) {
        log.info("Добовление новой подборки.");
        return service.addCompilation(compilation);
    }

    @PatchMapping("/{id}")
    public CompilationDto updateCompilation(@PathVariable long id,
                                            @Valid @RequestBody UpdateCompilationRequest updateCompilation) {
        log.info("Обновить информацию о подборке.");
        return service.updateCompilation(id, updateCompilation);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        log.info("Удаление подборки.");
        service.delete(id);
    }
}