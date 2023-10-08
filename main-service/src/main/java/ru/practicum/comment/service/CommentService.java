package ru.practicum.comment.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;

import java.util.List;

public interface CommentService {

    List<CommentDto> getComments(Pageable pageable);

    List<CommentDto> getCommentsByUserAndEvent(Long userId, Long eventId, Pageable pageable);

    List<CommentDto> getCommentsByEvent(Long eventId, Pageable pageable);

    CommentDto addComment(Long userId, Long eventId, NewCommentDto newComment);

    CommentDto updateComment(Long userId, Long id, NewCommentDto newComment);

    CommentDto getComment(Long id);

    void deleteComment(Long id);

    void deleteCommentByUser(Long userId, Long id);
}