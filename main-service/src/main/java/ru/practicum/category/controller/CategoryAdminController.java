package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {
    private final CategoryService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Добовление новой категории: {}", newCategoryDto);
        return service.addCategory(newCategoryDto);
    }

    @PatchMapping("/{id}")
    public CategoryDto update(@PathVariable Long id,
                             @Valid @RequestBody CategoryDto categoryDto) {
        log.info("Изменение категории: {}", categoryDto);
        return service.update(id, categoryDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("Удаление категории: {}", id);
        service.delete(id);
    }
}