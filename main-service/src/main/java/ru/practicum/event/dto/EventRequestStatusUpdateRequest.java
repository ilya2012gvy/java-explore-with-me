package ru.practicum.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.RequestStateAction;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateRequest {
    @NotEmpty
    List<Long> requestIds;
    @NotNull
    RequestStateAction status;
}