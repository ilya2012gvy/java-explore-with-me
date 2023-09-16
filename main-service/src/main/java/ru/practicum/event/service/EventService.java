package ru.practicum.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.enums.EventSort;
import ru.practicum.enums.EventState;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface EventService {

    List<EventFullDto> getAdminEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                      LocalDateTime start, LocalDateTime end, Pageable pageable);

    EventFullDto addEvent(long userId, NewEventDto newEventDto);

    EventFullDto getEvent(Long eventId, HttpServletRequest http);

    List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime start, LocalDateTime end,
                                  Boolean onlyAvailable, EventSort sort, Integer from, Integer size, HttpServletRequest http);

    EventFullDto getUserEventById(long userId, Long eventId);

    List<EventShortDto> getUserEvents(long userId, Pageable pageable);

    Map<Long, Long> getEventsViews(List<Event> events);

    EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest event);

    EventFullDto updateUserEvent(long userId, Long eventId, UpdateEventUserRequest event);

    Map<Long, Long> getConfirmedRequest(List<Event> events);
}