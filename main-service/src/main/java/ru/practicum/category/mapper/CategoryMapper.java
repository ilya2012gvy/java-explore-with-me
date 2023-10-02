package ru.practicum.category.mapper;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;

public interface CategoryMapper {
    static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName()).build();
    }

    static Category toCategory(CategoryDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName()).build();
    }

    static Category toNewCategory(NewCategoryDto category) {
        return Category.builder()
                .name(category.getName()).build();
    }
}