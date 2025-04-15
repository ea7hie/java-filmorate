package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.MpaRatings;

import java.util.Collection;

public interface MpaStorage {
    Collection<MpaRatings> getAllMpaRatings();

    MpaRatings getMpaRatingByIndex(int id);
}