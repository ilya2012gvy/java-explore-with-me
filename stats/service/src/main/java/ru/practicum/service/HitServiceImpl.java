package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.HitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.Hit;
import ru.practicum.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.mapper.HitMapper.toDto;
import static ru.practicum.mapper.HitMapper.toHit;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HitServiceImpl implements HitService {
    private final HitRepository repository;

    @Override
    @Transactional
    public HitDto create(HitDto dto) {
        Hit hit = repository.save(toHit(dto));
        return toDto(hit);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (uris.isEmpty() && unique) {
            return repository.getUniqueUrisStats(start, end);
        }
        if (uris.isEmpty()) {
            return repository.getAllUniqueUrisStats(start, end);
        }
        if (unique) {
            return repository.getUrisStats(start, end, uris);
        } else {
            return repository.getAllUrisStats(start, end, uris);
        }
    }
}