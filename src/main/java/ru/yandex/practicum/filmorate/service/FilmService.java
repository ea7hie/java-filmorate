package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.Collection;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addLikeFilm(long idOfFilm, long idOfUser) {
        return filmStorage.addLikeFilm(idOfFilm, idOfUser);
    }

    public Film deleteLikeFilm(long idOfFilm, long idOfUser) {
        return filmStorage.deleteLikeFilm(idOfFilm, idOfUser);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(long idOfFilm) {
        return filmStorage.getFilmById(idOfFilm);
    }

    public Collection<Film> getMostLikedFilms(int amount) {
        return filmStorage.getMostLikedFilms(amount);
    }

    public Film addFilm(Film newFilm) {
        return filmStorage.addFilm(newFilm);
    }

    public Film updateFilm(Film newFilm) {
        return filmStorage.updateFilm(newFilm);
    }

    public Film deleteFilmById(long idForDelete) {
        return filmStorage.deleteFilm(idForDelete);
    }
}