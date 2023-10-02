package ru.practicum.event.location.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Table(name = "locations")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @Column(name = "lat")
    Float lat;
    @Column(name = "lon")
    Float lon;
}