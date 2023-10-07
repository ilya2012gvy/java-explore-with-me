package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.CommentService;
import ru.practicum.pageable.ConvertPageable;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/comments")
@RequiredArgsConstructor
public class CommentPublicController {
    private final CommentService service;

    @GetMapping
    public List<CommentDto> getCommentsByEvent(
            @RequestParam Long eventId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получение всех комментариев к событию: {}", eventId);
        return service.getCommentsByEvent(eventId, ConvertPageable.toMakePage(from, size));
    }

    @GetMapping("/{id}")
    public CommentDto getComment(@PathVariable Long id) {
        log.info("Вывод комментариев с id: {}", id);
        return service.getComment(id);
    }
}