package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.pageable.ConvertPageable;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class EventPrivateController {
    private final EventService service;

    @GetMapping
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                             @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получение событий, добавленых текущим пользователем.");
        return service.getUserEvents(userId, ConvertPageable.toMakePage(from, size));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable long userId, @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Добовление нового события.");
        return service.addEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getUserEventById(@PathVariable long userId, @PathVariable Long eventId) {
        log.info("Получение новой информации о событии добавленным текущим пользователем.");
        return service.getUserEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateUserEvent(@PathVariable long userId, @PathVariable Long eventId,
                                            @Valid @RequestBody UpdateEventUserRequest updateEvent) {
        log.info("Изменение события добавленного текущим пользователем.");
        return service.updateUserEvent(userId, eventId, updateEvent);
    }
}