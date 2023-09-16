package ru.practicum.event.request.service;

import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getListEventRequest(Long userId);

    ParticipationRequestDto createEventRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelEventRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getEventRequestsByEventOwner(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateEventRequestByEventOwner(
            Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatus);
}