package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.MpaRatings;

import java.util.Collection;
import java.util.Optional;

public interface MpaStorage {
    Collection<MpaRatings> getAllMpaRatings();

    Optional<MpaRatings> getMpaRatingByIndex(int id);
}