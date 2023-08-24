package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty
    @NotNull
    @Column(name = "app")
    private String app;
    @NotEmpty
    @NotNull
    @Column(name = "uri")
    private String uri;
    @NotEmpty
    @NotNull
    @Column(name = "ip")
    private String ip;
    @NotNull
    @PastOrPresent
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}