package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static ru.practicum.comment.mapper.CommentMapper.toCommentDto;
import static ru.practicum.comment.mapper.CommentMapper.toCommentListDto;
import static ru.practicum.enums.EventState.PUBLISHED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CommentDto> getComments(Pageable pageable) {
        return toCommentListDto(commentRepository.findAll(pageable).toList());
    }

    @Override
    public List<CommentDto> getCommentsByUserAndEvent(Long userId, Long eventId, Pageable pageable) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("CommentServiceImpl: getCommentsByUserAndEvent User. Not Found 404"));

        List<Comment> comments;
        if (eventId != null) {
            comments = commentRepository.findAllByAuthorIdAndEventId(userId, eventId);
        } else {
            comments = commentRepository.findAllByAuthorId(userId);
        }

        return toCommentListDto(comments);
    }

    @Override
    public List<CommentDto> getCommentsByEvent(Long eventId, Pageable pageable) {
        eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("EventServiceImpl: getCommentsByUserAndEvent Event. Not Found 404"));

        return toCommentListDto(commentRepository.findAllByEventId(eventId, pageable));
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long eventId, NewCommentDto newComment) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("CommentServiceImpl: addComment User. Not Found 404"));
        Event event = eventRepository.findByIdAndState(eventId, PUBLISHED).orElseThrow(() ->
                new NotFoundException("EventServiceImpl: addComment Event. Not Found 404"));

        Comment comment = Comment.builder()
                .text(newComment.getText())
                .author(user)
                .event(event)
                .createdOn(LocalDateTime.now()).build();

        return toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long id, NewCommentDto newComment) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("CommentServiceImpl: updateComment User. Not Found 404"));
        Comment comment = commentRepository.findById(id).orElseThrow(() ->
                new NotFoundException("CommentServiceImpl: updateComment Comment. Not Found 404"));

        if (!Objects.equals(userId, id)) {
            throw new ForbiddenException("Пользователь не является владельцем комментария.");
        }

        comment.setText(newComment.getText());

        return toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto getComment(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() ->
                new NotFoundException("CommentServiceImpl: getComment Comment. Not Found 404"));

        return toCommentDto(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteCommentByUser(Long userId, Long id) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("CommentServiceImpl: deleteCommentByUser User. Not Found 404"));

        if (!Objects.equals(userId, id)) {
            throw new ForbiddenException("Пользователь не является владельцем комментария.");
        }

        commentRepository.deleteById(id);
    }
}