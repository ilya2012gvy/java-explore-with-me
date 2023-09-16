package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;
import ru.practicum.pageable.ConvertPageable;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
public class CategoryPublicController {
    private final CategoryService service;

    @GetMapping
    public List<CategoryDto> findAll(
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получение категории.");
        return service.findAll(ConvertPageable.toMakePage(from, size));
    }

    @GetMapping("/{id}")
    public CategoryDto findById(@PathVariable Long id) {
        log.info("Получение информации о категории по её индентификатору: {}", id);
        return service.findById(id);
    }
}