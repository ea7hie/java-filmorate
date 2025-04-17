package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Genres;

import java.util.Collection;

public interface GenreStorage {
    Collection<Genres> getAllGenres();

    Genres getGenreByIndex(int id);
}
