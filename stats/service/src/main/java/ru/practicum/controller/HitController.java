package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.service.HitService;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class HitController {
    private final HitService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto create(@RequestBody HitDto hitDto) {
        log.info("Сохранение информации о том, что на uri конкретного сервиса был отправлен запрос пользователем.");
        return service.create(hitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                       @RequestParam @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                       @RequestParam(defaultValue = "") List<String> uris,
                                       @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Получение статистики по посещениям.");
        return service.getStats(start, end, uris, unique);
    }
}