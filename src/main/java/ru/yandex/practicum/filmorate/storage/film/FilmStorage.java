package ru.yandex.practicum.filmorate.storage.film;

import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Film getFilmById(long id);

    Film addFilm(@Valid Film newFilmForAdd);

    Film updateFilm(@Valid Film newFilmForUpdate);

    Film deleteFilm(long idFilmForDelete);

    Collection<Film> getMostLikedFilms(int count);
}