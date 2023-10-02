package ru.practicum;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HitDto {
    Long id;
    @NotBlank
    @Size(max = 255)
    String app;
    @NotBlank
    @Size(max = 255)
    String uri;
    @NotBlank
    @Size(max = 15)
    String ip;
    @PastOrPresent
    String timestamp;
}