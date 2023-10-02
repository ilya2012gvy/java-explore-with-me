package ru.practicum.pageable;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.validation.ValidationException;

public class ConvertPageable extends PageRequest {

    protected ConvertPageable(int page, int size, Sort sort) {
        super(page, size, sort);
    }

    public static Pageable toMakePage(Integer from, Integer size) {
        if (from == null || size == null) {
            return null;
        }

        if (size <= 0 || from < 0) {
            throw new ValidationException();
        }

        int page = from / size;
        return PageRequest.of(page, size);
    }
}