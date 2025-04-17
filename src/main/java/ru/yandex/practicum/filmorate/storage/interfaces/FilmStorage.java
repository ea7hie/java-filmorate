package ru.yandex.practicum.filmorate.storage.interfaces;

import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Film getFilmById(long id);

    Film addFilm(@Valid Film newFilmForAdd);

    Film updateFilm(@Valid Film newFilmForUpdate);

    Film deleteFilm(long idFilmForDelete);

    List<Film> getMostLikedFilms(int count);

    Film addLikeFilm(long idOfFilm, long idOfUser);

    Film deleteLikeFilm(long idOfFilm, long idOfUser);
}