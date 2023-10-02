package ru.practicum.event.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.request.dto.ParticipationRequestDto;
import ru.practicum.event.request.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService service;

    @GetMapping("/requests")
    public List<ParticipationRequestDto> getListEventRequest(@PathVariable Long userId) {
        log.info("Получение информации о заявках текущего пользователя на участие в чужих событиях.");
        return service.getListEventRequest(userId);
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createEventRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("Добовление запроса текущего пользователя на участие в событие.");
        return service.createEventRequest(userId, eventId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelEventRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Отмена своего запроса на участие в событие.");
        return service.cancelEventRequest(userId, requestId);
    }

    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequestsByEventOwner(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Получение информации о запросах на участие в событии в текущего пользователя.");
        return service.getEventRequestsByEventOwner(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateEventRequestByEventOwner(
            @PathVariable long userId, @PathVariable Long eventId,
            @RequestBody @Valid EventRequestStatusUpdateRequest eventRequestStatus) {
        log.info("Изменение статуса заявок на участие в событие текущего пользователя.");
        return service.updateEventRequestByEventOwner(userId, eventId, eventRequestStatus);
    }
}