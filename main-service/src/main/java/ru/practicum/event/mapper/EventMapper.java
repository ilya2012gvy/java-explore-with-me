package ru.practicum.event.mapper;

import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.location.model.Location;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

import static ru.practicum.category.mapper.CategoryMapper.toCategoryDto;
import static ru.practicum.enums.EventState.PENDING;
import static ru.practicum.event.location.mapper.LocationMapper.toLocation;
import static ru.practicum.user.mapper.UserMapper.toUserShortDto;

public interface EventMapper {
    static Event toEvent(NewEventDto event, Category category, LocalDateTime now, User user, Location location) {
        return Event.builder()
                .annotation(event.getAnnotation())
                .category(category)
                .createdOn(now)
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(user)
                .location(location)
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(null)
                .requestModeration(event.getRequestModeration())
                .state(PENDING)
                .title(event.getTitle()).build();
    }

    static EventFullDto toEventFull(Event event, Long views) {
        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(toUserShortDto(event.getInitiator()))
                .location(toLocation(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState().toString())
                .title(event.getTitle())
                .views(views).build();
    }

    static EventFullDto toEventFullDto(Event event, Long confirmedRequests, Long views) {
        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(toCategoryDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(toUserShortDto(event.getInitiator()))
                .location(toLocation(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState().toString())
                .title(event.getTitle())
                .views(views).build();
    }

    static EventShortDto toEventShortDto(Event event, Long confirmedRequests, Long views) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(toCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .initiator(toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(views)
                .confirmedRequests(confirmedRequests).build();
    }

    static EventShortDto toEventShort(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .eventDate(event.getEventDate())
                .paid(event.getPaid())
                .title(event.getTitle()).build();
    }
}