package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HitDto {
    private Long id;
    @NotBlank
    @Size(max = 255)
    private String app;
    @NotBlank
    @Size(max = 255)
    private String uri;
    @NotBlank
    @Size(max = 15)
    private String ip;
    @PastOrPresent
    private String timestamp;
}