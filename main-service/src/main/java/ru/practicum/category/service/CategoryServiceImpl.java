package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.category.mapper.CategoryMapper.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    private final EventRepository eventRepository;

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        return repository.findAll(pageable).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto findById(long id) {
        Category category = repository.findById(id).orElseThrow(() ->
                new NotFoundException("CategoryServiceImpl: findById Not Found 404"));

        return toCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto addCategory(NewCategoryDto category) {
        if (repository.existsByNameAndIdIsNot(category.getName(), 0)) {
            throw new ForbiddenException("Имя уже используется.");
        }
        return toCategoryDto(repository.save(toNewCategory(category)));
    }

    @Override
    @Transactional
    public CategoryDto update(long id, CategoryDto category) {
        repository.findById(id).orElseThrow(() ->
                new NotFoundException("CategoryServiceImpl: update Not Found 404"));

        if (repository.existsByNameAndIdIsNot(category.getName(), id)) {
            throw new ForbiddenException("Имя уже используется.");
        }

        category.setId(id);

        return  toCategoryDto(repository.save(toCategory(category)));
    }

    @Override
    @Transactional
    public void delete(long id) {
        Category category = repository.findById(id).orElseThrow(() ->
                new NotFoundException("CategoryServiceImpl: delete Not Found 404"));

        if (eventRepository.findAllByCategoryId(id).size() != 0) {
            throw new ForbiddenException("Существуют события, удаление запрещено!");
        }

        repository.delete(category);
    }
}