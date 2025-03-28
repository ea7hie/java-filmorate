package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film addLikeFilm(long idOfFilm, long idOfUser) {
        checkUserIsAdded(idOfUser);
        checkFilmIsAdded(idOfFilm);

        Set<Long> idsOfAllUsersWhoLike = filmStorage.getFilmById(idOfFilm).getIdsOfAllUsersWhoLike();
        idsOfAllUsersWhoLike.add(idOfUser);
        return filmStorage.getFilmById(idOfFilm);
    }

    public Film deleteLikeFilm(long idOfFilm, long idOfUser) {
        checkUserIsAdded(idOfUser);
        checkFilmIsAdded(idOfFilm);

        Set<Long> idsOfAllUsersWhoLike = filmStorage.getFilmById(idOfFilm).getIdsOfAllUsersWhoLike();
        idsOfAllUsersWhoLike.remove(idOfUser);
        return filmStorage.getFilmById(idOfFilm);
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

    private void checkUserIsAdded(long idOfUserForCheck) {
        if (userStorage.getUserById(idOfUserForCheck) == null) {
            throw new NotFoundException(String.format("Пользователя для с id=%d не найдено", idOfUserForCheck));
        }
    }

    private void checkFilmIsAdded(long idOfFilmForCheck) {
        if (filmStorage.getFilmById(idOfFilmForCheck) == null) {
            throw new NotFoundException(String.format("Фильма для с id=%d не найдено", idOfFilmForCheck));
        }
    }
}