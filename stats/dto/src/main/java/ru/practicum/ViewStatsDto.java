package ru.practicum;

import lombok.Data;

@Data
public class ViewStatsDto {
    public ViewStatsDto(String app, String uri, Long hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits;
    }

    private String app;
    private String uri;
    private Long hits;
}
