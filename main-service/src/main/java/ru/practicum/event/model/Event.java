package ru.practicum.event.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.model.Category;
import ru.practicum.enums.EventState;
import ru.practicum.event.location.model.Location;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @Column(name = "annotation", length = 2000)
    String annotation;
    @JoinColumn(name = "category_id")
    @ManyToOne(fetch = FetchType.EAGER)
    Category category;
    @Column(name = "created_on")
    LocalDateTime createdOn;
    @Column(name = "description", length = 7000)
    String description;
    @Column(name = "event_date")
    LocalDateTime eventDate;
    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.EAGER)
    User initiator;
    @JoinColumn(name = "location_id")
    @ManyToOne(fetch = FetchType.EAGER)
    Location location;
    @Column(name = "paid")
    Boolean paid;
    @Column(name = "participant_limit")
    Integer participantLimit;
    @Column(name = "published_on")
    LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    EventState state;
    @Column(name = "title", length = 120)
    String title;
    @Column(name = "confirmed_requests")
    Long confirmedRequests;
}