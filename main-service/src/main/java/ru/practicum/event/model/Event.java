package ru.practicum.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "annotation", length = 2000)
    private String annotation;
    @JoinColumn(name = "category_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Category category;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @Column(name = "description", length = 7000)
    private String description;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private User initiator;
    @JoinColumn(name = "location_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Location location;
    @Column(name = "paid")
    private Boolean paid;
    @Column(name = "participant_limit")
    private Integer participantLimit;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private EventState state;
    @Column(name = "title", length = 120)
    private String title;
    @Column(name = "confirmed_requests")
    private Long confirmedRequests;
}