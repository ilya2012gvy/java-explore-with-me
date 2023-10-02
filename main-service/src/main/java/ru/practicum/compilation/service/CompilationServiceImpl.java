package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.compilation.mapper.CompilationMapper.toCompilationDto;
import static ru.practicum.compilation.mapper.CompilationMapper.toNewCompilationDto;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final EventService service;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Pageable pageable) {
        List<Compilation> compilations;
        if (pinned != null) {
            compilations = compilationRepository.findAllByPinned(pinned, pageable);
        } else {
            compilations = compilationRepository.findAll(pageable).toList();
        }

        Set<Event> event = new HashSet<>();
        compilations.forEach(compilation -> event.addAll(compilation.getEvents()));

        Map<Long, EventShortDto> events = new HashMap<>();
        toEventsShortDto(new ArrayList<>(event))
                .forEach(event1 -> events.put(event1.getId(), event1));


        List<CompilationDto> list = new ArrayList<>();

        compilations.forEach(compilation -> {
            List<EventShortDto> eventShort = new ArrayList<>();
            compilation.getEvents()
                    .forEach(event1 -> eventShort.add(events.get(event1.getId())));
            list.add(toCompilationDto(compilation, eventShort));
        });

        return list;
    }

    @Override
    public CompilationDto findById(long id) {
        Compilation compilation = compilationRepository.findById(id).orElseThrow(
                () -> new NotFoundException("CompilationServerImpl: getById. Not Found 404"));

        List<EventShortDto> events = toEventsShortDto(compilation.getEvents());

        return toCompilationDto(compilation, events);
    }

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilation) {
        List<Event> events = new ArrayList<>();

        if (!newCompilation.getEvents().isEmpty()) {
            events = eventRepository.findByIdIn(newCompilation.getEvents());
            if (events.size() != newCompilation.getEvents().size()) {
                throw new NotFoundException("Некоторые события не найдены!");
            }
        }

        Compilation compilation = compilationRepository.save(toNewCompilationDto(newCompilation, events));

        return findById(compilation.getId());
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(long id, UpdateCompilationRequest updateCompilation) {
        Compilation compilation = compilationRepository.findById(id).orElseThrow(
                () -> new NotFoundException("CompilationServerImpl: updateCompilation. Not Found 404"));

        if (updateCompilation.getTitle() != null) {
            compilation.setTitle(updateCompilation.getTitle());
        }

        if (updateCompilation.getPinned() != null) {
            compilation.setPinned(updateCompilation.getPinned());
        }

        if (updateCompilation.getEvents() != null) {
            List<Event> events = eventRepository.findByIdIn(updateCompilation.getEvents());

            if (events.size() != updateCompilation.getEvents().size()) {
                throw new NotFoundException("Некоторые события не найдены!");
            }

            compilation.setEvents(events);
        }

        compilationRepository.save(compilation);

        return findById(id);
    }

    @Override
    @Transactional
    public void delete(long id) {
        compilationRepository.deleteById(id);
    }

    private List<EventShortDto> toEventsShortDto(List<Event> events) {
        Map<Long, Long> views = service.getEventsViews(events);
        Map<Long, Long> confirmedRequests = service.getConfirmedRequest(events);

        return events.stream()
                .map((event) -> EventMapper.toEventShortDto(
                        event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        views.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }
}