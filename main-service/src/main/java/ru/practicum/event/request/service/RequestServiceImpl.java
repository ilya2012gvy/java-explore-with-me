package ru.practicum.event.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.enums.RequestStateAction;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.request.dto.ParticipationRequestDto;
import ru.practicum.event.request.mapper.RequestMapper;
import ru.practicum.event.request.model.ParticipationRequest;
import ru.practicum.event.request.repository.RequestRepository;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.enums.EventState.PUBLISHED;
import static ru.practicum.enums.RequestStatus.*;
import static ru.practicum.event.request.mapper.RequestMapper.toRequestDto;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventService eventService;

    @Override
    public List<ParticipationRequestDto> getListEventRequest(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("RequestServiceImpl: getListEventRequest. User: Not Found 404"));

        return toRequest(requestRepository.findAllByRequesterId(userId));
    }

    @Override
    @Transactional
    public ParticipationRequestDto createEventRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("RequestServiceImpl: createEventRequest. User: Not Found 404"));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("RequestServiceImpl: createEventRequest. Event: Not Found 404"));

        validCreateEventRequest(event, userId, eventId);

        ParticipationRequest newRequest = new ParticipationRequest();
        newRequest.setRequester(user);
        newRequest.setEvent(event);
        newRequest.setCreated(LocalDateTime.now());

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            newRequest.setStatus(CONFIRMED);
        } else {
            newRequest.setStatus(PENDING);
        }

        return toRequestDto(requestRepository.save(newRequest));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelEventRequest(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("RequestServiceImpl: cancelEventRequest. User: Not Found 404"));
        ParticipationRequest request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("RequestServiceImpl: cancelEventRequest. Request: Not Found 404"));

        if (!Objects.equals(request.getRequester().getId(), userId)) {
            throw new ForbiddenException("Это ваше мероприятие!");
        }

        request.setStatus(CANCELED);

        return toRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getEventRequestsByEventOwner(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("RequestServiceImpl: createEventRequest. User: Not Found 404"));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("RequestServiceImpl: createEventRequest. Event: Not Found 404"));

        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ForbiddenException("Это ваше мероприятие!");
        }

        List<ParticipationRequest> result = requestRepository.findByEvent(event);
        return result.stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateEventRequestByEventOwner(Long userId, Long eventId,
                                                                         EventRequestStatusUpdateRequest eventRequestStatus) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("RequestServiceImpl: updateEventRequestByEventOwner. User: Not Found 404"));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("RequestServiceImpl: updateEventRequestByEventOwner. Event: Not Found 404"));

        validEventRequestStatusUpdateResult(user, event, eventRequestStatus);

        List<ParticipationRequest> requests = requestRepository.findAllByIdIn(eventRequestStatus.getRequestIds());

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        Long confirmedRequestsCount = eventService.getConfirmedRequest(List.of(event)).getOrDefault(event.getId(), 0L);
        if (confirmedRequestsCount >= event.getParticipantLimit()) {
            throw new ForbiddenException("Достигнут лимит участников!");
        }

        if (RequestStateAction.REJECTED.equals(eventRequestStatus.getStatus())) {
            for (ParticipationRequest request : requests) {
                request.setStatus(REJECTED);
                rejectedRequests.add(RequestMapper.toRequestDto(requestRepository.save(request)));
            }
        } else {
            for (ParticipationRequest request : requests) {
                if (confirmedRequestsCount >= event.getParticipantLimit()) {
                    request.setStatus(REJECTED);
                    rejectedRequests.add(toRequestDto(requestRepository.save(request)));
                } else {
                    request.setStatus(CONFIRMED);
                    confirmedRequests.add(toRequestDto(requestRepository.save(request)));
                    confirmedRequestsCount++;
                }
            }
        }

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests).build();
    }

    private List<ParticipationRequestDto> toRequest(List<ParticipationRequest> requests) {
        return requests.stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    private void validCreateEventRequest(Event event, Long userId, Long eventId) {
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ForbiddenException("Это ваше мероприятие!");
        }

        if (!event.getState().equals(PUBLISHED)) {
            throw new ForbiddenException("Событие не опубликовано!");
        }

        Optional<ParticipationRequest> old = requestRepository.findByEventIdAndRequesterId(eventId, userId);
        if (old.isPresent()) {
            throw new ForbiddenException("Уже запросили!");
        }

        Long confirmedRequestsCount = eventService.getConfirmedRequest(List.of(event)).getOrDefault(event.getId(), 0L);
        if (event.getParticipantLimit() != 0 && confirmedRequestsCount >= event.getParticipantLimit()) {
            throw new ForbiddenException("Достигнут лимит участников!");
        }
    }

    private void validEventRequestStatusUpdateResult(User user, Event event, EventRequestStatusUpdateRequest eventRequestStatus) {
        if (!Objects.equals(event.getInitiator(), user)) {
            throw new ForbiddenException("Вы не являетесь владельцем мероприятия!");
        }

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            EventRequestStatusUpdateResult.builder().build();
            return;
        }

        List<ParticipationRequest> requests = requestRepository.findAllByIdIn(eventRequestStatus.getRequestIds());
        if (requests.size() != eventRequestStatus.getRequestIds().size()) {
            throw new ForbiddenException("Некоторые заявки на участие: " + requests.size() + " не найдены!");
        }

        if (!requests.stream()
                .map(ParticipationRequest::getStatus)
                .allMatch(PENDING::equals)) {
            throw new ForbiddenException("Изменить можно только ожидающие заявки!");
        }
    }
}