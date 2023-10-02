package ru.practicum.user.mapper;

import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;

public interface UserMapper {
    static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail()).build();
    }

    static User toUser(NewUserRequest user) {
        return User.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }
}