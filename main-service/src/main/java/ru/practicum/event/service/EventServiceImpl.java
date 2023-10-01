package ru.practicum.event.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.enums.EventSort;
import ru.practicum.enums.EventState;
import ru.practicum.event.dto.*;
import ru.practicum.event.location.dto.LocationDto;
import ru.practicum.event.location.model.Location;
import ru.practicum.event.location.repository.LocationRepository;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.request.repository.RequestRepository;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.enums.EventSort.EVENT_DATE;
import static ru.practicum.enums.EventSort.VIEWS;
import static ru.practicum.enums.EventState.*;
import static ru.practicum.enums.RequestStatus.CONFIRMED;
import static ru.practicum.event.location.mapper.LocationMapper.toLocationDto;
import static ru.practicum.event.mapper.EventMapper.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final StatsClient statsClient;
    private final ObjectMapper objectMapper;

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Value(value = "${app.name}")
    private String appName;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<EventFullDto> getAdminEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                             LocalDateTime start, LocalDateTime end, Pageable pageable) {
        checkStartIsBeforeEnd(start, end);

        Page<Event> events = eventRepository.findAll(((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (users != null && !users.isEmpty()) {
                predicates.add(root.get("initiator").in(users));
            }
            if (states != null && !states.isEmpty()) {
                predicates.add(root.get("state").in(states));
            }
            if (categories != null && !categories.isEmpty()) {
                predicates.add(root.get("category").in(categories));
            }
            if (start != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), start));
            }
            if (end != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), end));
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        }), pageable);

        Map<Long, Long> confirmedRequest = getConfirmedRequest(events.toList());
        Map<Long, Long> views = getEventsViews(events.toList());

        return events.stream()
                .map(event -> toEventFullDto(event, confirmedRequest.getOrDefault(event.getId(), 0L),
                        views.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto addEvent(long userId, NewEventDto newEventDto) {
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() ->
                new NotFoundException("EventServiceImpl: addEvent Category. Not Found 404"));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("EventServiceImpl: addEvent User. Not Found 404"));
        Location location = getOrSaveLocation(newEventDto.getLocation());

        checkNewEventDate(newEventDto.getEventDate(), LocalDateTime.now().plusHours(2));

        Event event = eventRepository.save(toEvent(newEventDto, category, LocalDateTime.now(), user, location));

        return toEventFull(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEvent(Long id, HttpServletRequest http) {
        Event event = eventRepository.findByIdAndState(id, PUBLISHED).orElseThrow(() ->
                new NotFoundException("EventServiceImpl: getEvent Event. Not Found 404"));

        create(http);

        return EventMapper.toEventFull(event, 1L);
    }

    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime start,
                                         LocalDateTime end, Boolean onlyAvailable, EventSort sort, Integer from, Integer size, HttpServletRequest http) {

        checkStartIsBeforeEnd(start, end);

        List<Event> events = getEventsList(text, categories, paid, start, end, from, size);

        Map<Long, Integer> eventsParticipantLimit = new HashMap<>();
        events.forEach(event -> eventsParticipantLimit.put(event.getId(), event.getParticipantLimit()));

        List<EventShortDto> event = toEventsShortDto(events);

        if (onlyAvailable) {
            event = event.stream()
                    .filter(eventShort -> (eventsParticipantLimit.get(eventShort.getId()) == 0 ||
                            eventsParticipantLimit.get(eventShort.getId()) > eventShort.getConfirmedRequests()))
                    .collect(Collectors.toList());
        }

        if (sort(sort, VIEWS)) {
            event.sort(Comparator.comparing(EventShortDto::getViews));
        } else if (sort(sort, EVENT_DATE)) {
            event.sort(Comparator.comparing(EventShortDto::getEventDate));
        }

        create(http);

        return event;
    }

    @Override
    public EventFullDto getUserEventById(long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("EventServiceImpl: getUserEventById User. Not Found 404"));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("EventServiceImpl: getUserEventById Event. Not Found 404"));

        return toEventFull(event);
    }

    @Override
    public List<EventShortDto> getUserEvents(long userId, Pageable pageable) {
        List<Event> events = eventRepository.findByInitiatorId(userId, pageable);
        return events.stream()
                .map(EventMapper::toEventShort)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, Long> getEventsViews(List<Event> events) {
        Map<Long, Long> views = new HashMap<>();

        List<Event> published = events.stream()
                .filter(event -> event.getPublishedOn() != null)
                .collect(Collectors.toList());

        Optional<LocalDateTime> minPublishedOn = published.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);

        if (minPublishedOn.isPresent()) {
            LocalDateTime start = minPublishedOn.get();
            LocalDateTime end = LocalDateTime.now();
            List<String> uris = published.stream()
                    .map(Event::getId)
                    .map(id -> ("/events/" + id))
                    .collect(Collectors.toList());

            List<ViewStatsDto> stats = getStats(start, end, uris, true);
            stats.forEach(stat -> {
                Long eventId = Long.parseLong(stat.getUri().split("/", 0)[2]);
                views.put(eventId, views.getOrDefault(eventId, 0L) + stat.getHits());
            });
        }
        return views;
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        ResponseEntity<Object> response = statsClient.getStatistic(start, end, uris, unique);

        try {
            return Arrays.asList(objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), ViewStatsDto[].class));
        } catch (IOException exception) {
            throw new ClassCastException(exception.getMessage());
        }
    }

    @Override
    @Transactional
    public EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest updateEvent) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("EventServiceImpl: updateAdminEvent Not Found 404"));

        checkNewEventDate(updateEvent.getEventDate(), LocalDateTime.now().plusHours(2));

        if (!event.getState().equals(PENDING)) {
            throw new ForbiddenException("Запрещено менять состояние события!");
        }

        if (updateEvent.getAnnotation() != null) {
            event.setAnnotation(updateEvent.getAnnotation());
        }

        if (updateEvent.getDescription() != null) {
            event.setDescription(updateEvent.getDescription());
        }

        if (updateEvent.getPaid() != null) {
            event.setPaid(updateEvent.getPaid());
        }

        if (updateEvent.getParticipantLimit() != null) {
            checkNewLimit(updateEvent.getParticipantLimit(),
                    getConfirmedRequest(List.of(event)).getOrDefault(eventId, 0L));

            event.setParticipantLimit(updateEvent.getParticipantLimit());
        }

        if (updateEvent.getCategory() != null) {
            Category category = categoryRepository.findById(updateEvent.getCategory()).orElseThrow(() ->
                    new NotFoundException("EventServiceImpl: updateAdminEvent Category. Not Found 404"));

            event.setCategory(category);
        }

        if (updateEvent.getStateAction() != null) {
            switch (updateEvent.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    event.setState(REJECTED);
                    break;
            }
        }

        if (updateEvent.getTitle() != null) {
            event.setTitle(updateEvent.getTitle());
        }

        return toEventFull(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto updateUserEvent(long userId, Long eventId, UpdateEventUserRequest updateEvent) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("EventServiceImpl: updateUserEvent User. Not Found 404"));
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(() ->
                new NotFoundException("EventServiceImpl: updateUserEvent Event. Not Found 404"));

        checkNewEventDate(updateEvent.getEventDate(), LocalDateTime.now().plusHours(2));

        if (event.getState().equals(PUBLISHED)) {
            throw new ForbiddenException("Запрещено менять состояние события!");
        }

        if (event.getInitiator().getId() != userId) {
            throw new ValidationException("Запрос составлен некорректно!");
        }

        if (updateEvent.getEventDate() != null) {
            if (updateEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ValidationException("Событие не удовлетворяет правилам редактирования");
            }
            event.setEventDate(updateEvent.getEventDate());
        }

        if (updateEvent.getCategory() != null) {
            Category category = categoryRepository.findById(updateEvent.getCategory()).orElseThrow(() ->
                    new NotFoundException("EventServiceImpl: updateUserEvent Category. Not Found 404"));

            event.setCategory(category);
        }

        if (updateEvent.getStateAction() != null) {
            switch (updateEvent.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(PENDING);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case CANCEL_REVIEW:
                    event.setState(CANCELED);
                    break;
            }
        }

        return toEventFull(eventRepository.save(event));
    }

    @Override
    public Map<Long, Long> getConfirmedRequest(List<Event> events) {
        Map<Long, Long> confirmedRequests = new HashMap<>();
        requestRepository.getConfirmedRequests(CONFIRMED, events)
                .forEach(element -> confirmedRequests.put((Long) element[0], (Long) element[1]));
        return confirmedRequests;
    }

    private List<EventFullDto> toEventsFullDto(List<Event> events) {
        Map<Long, Long> views = getEventsViews(events);
        Map<Long, Long> confirmedRequests = getConfirmedRequest(events);

        return events.stream()
                .map((event) -> toEventFullDto(
                        event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        views.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    private EventFullDto toEventFull(Event event) {
        return toEventsFullDto(List.of(event)).get(0);
    }

    private List<EventShortDto> toEventsShortDto(List<Event> events) {
        Map<Long, Long> confirmedRequests = getConfirmedRequest(events);
        Map<Long, Long> views = getEventsViews(events);

        return events.stream()
                .map((event) -> toEventShortDto(
                        event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        views.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    public void create(HttpServletRequest http) {
        statsClient.create(appName, http.getRequestURI(), http.getRemoteAddr(),
                LocalDateTime.parse(LocalDateTime.now().format(formatter), formatter));
    }

    private List<Event> getEventsList(String text, List<Long> categories, Boolean paid, LocalDateTime start,
                                      LocalDateTime end, Integer from, Integer size) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = builder.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);
        Predicate criteria = builder.conjunction();

        if (text != null && !text.isBlank()) {
            Predicate annotation = builder.like(builder.lower(root.get("annotation")), "%" + text.toLowerCase() + "%");
            Predicate description = builder.like(builder.lower(root.get("description")), "%" + text.toLowerCase() + "%");
            criteria = builder.and(criteria, builder.or(annotation, description));
        }

        if (categories != null && !categories.isEmpty()) {
            criteria = builder.and(criteria, root.get("category").in(categories));
        }

        if (paid != null) {
            criteria = builder.and(criteria, root.get("paid").in(paid));
        }

        if (start == null && end == null) {
            criteria = builder.and(criteria, builder.greaterThanOrEqualTo(root.get("eventDate"), LocalDateTime.now()));
        } else {
            if (start != null) {
                criteria = builder.and(criteria, builder.greaterThanOrEqualTo(root.get("eventDate"), start));
            }

            if (end != null) {
                criteria = builder.and(criteria, builder.lessThanOrEqualTo(root.get("eventDate"), end));
            }
        }

        criteria = builder.and(criteria, root.get("state").in(EventState.PUBLISHED));

        query.select(root).where(criteria);
        return entityManager.createQuery(query).setFirstResult(from).setMaxResults(size).getResultList();
    }

    private Location getOrSaveLocation(LocationDto locationDto) {
        Location newLocation = toLocationDto(locationDto);
        return locationRepository.findByLatAndLon(newLocation.getLat(), newLocation.getLon())
                .orElseGet(() -> locationRepository.save(newLocation));
    }

    private Boolean sort(EventSort sort, EventSort eventSort) {
        return sort != null && sort.equals(eventSort);
    }

    private void checkStartIsBeforeEnd(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new ValidationException("Некорректные параметры временного интервала!");
        }
    }

    private void checkNewEventDate(LocalDateTime date, LocalDateTime min) {
        if (date != null && date.isBefore(min)) {
            throw new ValidationException("Остается слишком мало времени!");
        }
    }

    private void checkNewLimit(Integer newLimit, Long eventLimit) {
        if (newLimit != 0 && eventLimit != 0 && (newLimit < eventLimit)) {
            throw new ValidationException("Новый лимит участников должен быть не меньше количества уже одобренных заявок!");
        }
    }
}
