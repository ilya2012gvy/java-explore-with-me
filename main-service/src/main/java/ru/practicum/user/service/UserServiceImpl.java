package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.user.mapper.UserMapper.toUser;
import static ru.practicum.user.mapper.UserMapper.toUserDto;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserDto> getUsers(List<Long> ids, Pageable pageable) {
        List<User> users;
        if (ids == null || ids.isEmpty()) {
            users = repository.findAll(pageable).toList();
        } else {
            users = repository.findAllByIdIn(ids, pageable);
        }
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto addUser(NewUserRequest userRequest) {
        if (repository.existsByName(userRequest.getName())) {
            throw new ForbiddenException("Имя уже используется.");
        }
        return toUserDto(repository.save(toUser(userRequest)));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        repository.deleteById(id);
    }
}