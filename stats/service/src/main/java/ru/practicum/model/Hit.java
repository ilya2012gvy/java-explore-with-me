package ru.practicum.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Entity
@Table(name = "hits")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @NotEmpty
    @NotNull
    @Column(name = "app")
    String app;
    @NotEmpty
    @NotNull
    @Column(name = "uri")
    String uri;
    @NotEmpty
    @NotNull
    @Column(name = "ip")
    String ip;
    @NotNull
    @PastOrPresent
    @Column(name = "timestamp")
    LocalDateTime timestamp;
}