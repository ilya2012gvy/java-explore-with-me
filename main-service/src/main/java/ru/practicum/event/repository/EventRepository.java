package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.enums.EventState;
import ru.practicum.event.model.Event;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    List<Event> findByInitiatorId(long userId, Pageable pageable);

    List<Event> findByIdIn(Collection<Long> id);

    Optional<Event> findByInitiatorIdAndId(Long userId, Long eventId);

    Optional<Event> findByIdAndState(Long id, EventState state);

    List<Event> findAllByCategoryId(Long id);
}