package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query("select new ru.practicum.ViewStatsDto(h.app, h.uri, count(distinct h.ip)) from Hit as h " +
            "where h.timestamp > ?1 and h.timestamp < ?2 " +
            "group by h.app, h.uri " +
            "order by count(distinct h.uri) desc")
    List<ViewStatsDto> getUniqueUrisStats(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.ViewStatsDto(h.app, h.uri, count(h.ip)) from Hit as h " +
            "where h.timestamp > ?1 and h.timestamp < ?2 " +
            "group by h.app, h.uri " +
            "order by count(*) desc")
    List<ViewStatsDto> getAllUniqueUrisStats(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.ViewStatsDto(h.app, h.uri, count(distinct h.ip)) from Hit as h " +
            "where h.timestamp > ?1 and h.timestamp < ?2 and h.uri in ?3 " +
            "group by h.app, h.uri " +
            "order by count(distinct h.uri) DESC")
    List<ViewStatsDto> getUrisStats(LocalDateTime start, LocalDateTime end, List<String> uri);

    @Query("select new ru.practicum.ViewStatsDto(h.app, h.uri, count(h.ip)) from Hit as h " +
            "where h.timestamp > ?1 and h.timestamp < ?2 and h.uri in ?3 " +
            "group by h.app, h.uri " +
            "order by count(*) desc")
    List<ViewStatsDto> getAllUrisStats(LocalDateTime start, LocalDateTime end, List<String> uri);
}