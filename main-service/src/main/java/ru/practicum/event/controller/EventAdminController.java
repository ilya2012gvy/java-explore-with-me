package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.enums.EventState;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.pageable.ConvertPageable;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
public class EventAdminController {
    private final EventService service;

    @PatchMapping("/{eventId}")
    public EventFullDto updateAdminEvent(@PathVariable Long eventId,
                                          @Valid @RequestBody UpdateEventAdminRequest updateEvent) {
        log.info("Редактирования данный события и его статус!");
        return service.updateAdminEvent(eventId, updateEvent);
    }

    @GetMapping
    public List<EventFullDto> getAdminEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Поиск событий!");
        return service.getAdminEvents(users, states, categories, start, end, ConvertPageable.toMakePage(from, size));
    }
}