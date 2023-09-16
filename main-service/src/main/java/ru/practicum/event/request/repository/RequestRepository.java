package ru.practicum.event.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.enums.RequestStatus;
import ru.practicum.event.model.Event;
import ru.practicum.event.request.model.ParticipationRequest;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByRequesterId(Long requesterId);

    Optional<ParticipationRequest> findByEventIdAndRequesterId(Long eventId, Long userId);

    List<ParticipationRequest> findAllByIdIn(List<Long> requestIds);

    List<ParticipationRequest> findByEvent(Event event);

    @Query("select p.event.id, count(p.id) " +
            "from ParticipationRequest p " +
            "where p.status = ?1 " +
            "and p.event in ?2 " +
            "group by p.event.id")
    List<Object[]> getConfirmedRequests(RequestStatus status, Collection<Event> events);
}