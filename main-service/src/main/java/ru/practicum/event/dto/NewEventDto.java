package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.location.dto.LocationDto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    String annotation;
    @NotNull
    Long category;
    @NotBlank
    @Size(min = 20, max = 7000)
    String description;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    LocalDateTime eventDate;
    @Valid
    @NotNull
    LocationDto location;
    @Builder.Default
    Boolean paid = false;
    @PositiveOrZero
    @Builder.Default
    Integer participantLimit = 0;
    @Builder.Default
    Boolean requestModeration = true;
    @NotBlank
    @Size(min = 3, max = 120)
    String title;
}