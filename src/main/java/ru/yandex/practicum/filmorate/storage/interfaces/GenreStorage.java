package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Genres;

import java.util.Collection;
import java.util.Optional;

public interface GenreStorage {
    Collection<Genres> getAllGenres();

    Optional<Genres> getGenreByIndex(int id);
}
