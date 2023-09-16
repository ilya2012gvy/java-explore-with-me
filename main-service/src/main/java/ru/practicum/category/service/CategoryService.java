package ru.practicum.category.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto findById(long id);

    CategoryDto addCategory(NewCategoryDto category);

    CategoryDto update(long id, CategoryDto category);

    void delete(long id);
}