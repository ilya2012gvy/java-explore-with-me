package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.location.dto.LocationDto;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {
    String annotation;
    CategoryDto category;
    Long confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdOn;
    String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    Long id;
    UserShortDto initiator;
    LocationDto location;
    Boolean paid;
    Integer participantLimit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime publishedOn;
    Boolean requestModeration;
    String state;
    String title;
    Long views;
}