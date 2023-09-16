package ru.practicum.event.request.mapper;

import ru.practicum.event.request.dto.ParticipationRequestDto;
import ru.practicum.event.request.model.ParticipationRequest;

public interface RequestMapper {
    static ParticipationRequestDto toRequestDto(ParticipationRequest request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .requester(request.getRequester().getId())
                .event(request.getEvent().getId())
                .status(request.getStatus().toString())
                .created(request.getCreated()).build();
    }
}