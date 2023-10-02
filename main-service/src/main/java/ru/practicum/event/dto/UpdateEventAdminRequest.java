package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.EventAdminStateAction;
import ru.practicum.event.location.dto.LocationDto;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000)
    String annotation;
    Long category;
    @Size(min = 20, max = 7000)
    String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    LocalDateTime eventDate;
    @Valid
    LocationDto location;
    Boolean paid;
    @PositiveOrZero
    Integer participantLimit;
    Boolean requestModeration;
    EventAdminStateAction stateAction;
    @Size(min = 3, max = 120)
    String title;
}