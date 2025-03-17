package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Film getFilmById(long id);

    Film addFilm(Film newFilmForAdd);

    Film updateFilm(Film newFilmForUpdate);

    Film deleteFilm(long idFilmForDelete);

    Collection<Film> getMostLikedFilms(int count);
}