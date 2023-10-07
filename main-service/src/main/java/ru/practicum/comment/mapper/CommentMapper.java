package ru.practicum.comment.mapper;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.model.Comment;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.event.mapper.EventMapper.toEventShortDto;
import static ru.practicum.user.mapper.UserMapper.toUserShortDto;

public interface CommentMapper {

     static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(toUserShortDto(comment.getAuthor()))
                .event(toEventShortDto(comment.getEvent(), null, null))
                .createdOn(comment.getCreatedOn()).build();
    }

    static List<CommentDto> toCommentListDto(List<Comment> comment) {
        return comment.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}