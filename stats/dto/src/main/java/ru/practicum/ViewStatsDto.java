package ru.practicum;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ViewStatsDto {
    public ViewStatsDto(String app, String uri, Long hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits;
    }

    String app;
    String uri;
    Long hits;
}
