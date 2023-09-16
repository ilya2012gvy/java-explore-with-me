package ru.practicum.event.location.mapper;

import ru.practicum.event.location.dto.LocationDto;
import ru.practicum.event.location.model.Location;

public interface LocationMapper {
    static Location toLocationDto(LocationDto location) {
        return Location.builder()
                .lon(location.getLon())
                .lat(location.getLat()).build();
    }

    static LocationDto toLocation(Location location) {
        return new LocationDto(location.getLat(), location.getLon());
    }
}