package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public void create(String name, String uri, String ip, LocalDateTime timestamp) {

        HitDto hit = HitDto.builder()
                .app(name)
                .uri(uri)
                .ip(ip)
                .timestamp(timestamp.format(formatter))
                .build();
        post("/hit", hit);
    }

    public ResponseEntity<Object> getStatistic(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        StringBuilder uriBuilder = new StringBuilder("/stats" + "?start={start}&end={end}");
        Map<String, Object> parameters = Map.of(
                "start", start.format(formatter),
                "end", end.format(formatter)
        );

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                uriBuilder.append("&uris=").append(uri);
            }
        }
        if (unique != null) {
            uriBuilder.append("&unique=").append(unique);
        }

        return get(uriBuilder.toString(), parameters);
    }
}