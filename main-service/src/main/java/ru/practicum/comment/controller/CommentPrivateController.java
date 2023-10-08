package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.service.CommentService;
import ru.practicum.pageable.ConvertPageable;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/comments")
@RequiredArgsConstructor
public class CommentPrivateController {
    private final CommentService service;

    @GetMapping
    public List<CommentDto> getCommentsByUserAndEvent(@PathVariable Long userId,
                                                      @RequestParam(defaultValue = "") Long eventId,
                                                      @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                      @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получение всех комментариев пользователя: {}", userId);
        return service.getCommentsByUserAndEvent(userId, eventId, ConvertPageable.toMakePage(from, size));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable Long userId, @RequestParam Long eventId,
                                 @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Добавление нового комментария!");
        return service.addComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{id}")
    public CommentDto updateComment(@PathVariable Long userId, @PathVariable Long id,
                                    @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Обновление комментария!");
        return service.updateComment(userId, id, newCommentDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByUser(@PathVariable Long userId, @PathVariable Long id) {
        log.info("Удаление комментария пользователем: {}", userId);
        service.deleteCommentByUser(userId, id);
    }
}