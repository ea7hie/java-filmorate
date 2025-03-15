package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    public Collection<Film> getAllFilms();

    public Film getFilmById(long id);

    public Film addFilm(Film newFilmForAdd);

    public Film updateFilm(Film newFilmForUpdate);

    public Film deleteFilm(long idFilmForDelete);
}