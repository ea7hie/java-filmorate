package ru.yandex.practicum.filmorate.storage.inMemory;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.comparators.FilmComparatorByAmountOfLikes;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Long, Film> allFilmsByIds = new HashMap<>();
    private long idForNewFilm = 0;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public Collection<Film> getAllFilms() {
        return allFilmsByIds.values().stream().toList();
    }

    @Override
    public Film getFilmById(long id) {
        if (allFilmsByIds.containsKey(id)) {
            return allFilmsByIds.get(id);
        }
        throw new NotFoundException(String.format("Фильма для отображения с id=%d не найдено", id));
    }

    @Override
    public List<Film> getMostLikedFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("Введено неверное число. Используйте целое число, большее нуля.");
        }
        count = Math.min(count, allFilmsByIds.size());
        return allFilmsByIds.values().stream()
                .sorted(new FilmComparatorByAmountOfLikes())
                .toList()
                .subList(0, count);
    }

    @Override
    public Film addFilm(@Valid Film newFilmForAdd) {
        newFilmForAdd.setId(getNextIdForFilm());
        allFilmsByIds.put(newFilmForAdd.getId(), newFilmForAdd);
        log.info("new film added");
        return newFilmForAdd;
    }

    @Override
    public Film updateFilm(@Valid Film newFilmForUpdate) {
        if (allFilmsByIds.containsKey(newFilmForUpdate.getId())) {
            newFilmForUpdate.setIdsOfAllUsersWhoLike(
                    allFilmsByIds.get(newFilmForUpdate.getId()).getIdsOfAllUsersWhoLike());
            allFilmsByIds.put(newFilmForUpdate.getId(), newFilmForUpdate);
            log.info("old film updated");
            return newFilmForUpdate;
        }
        log.info("not founded the film by id {}", newFilmForUpdate.getId());
        throw new NotFoundException("Не найдено фильма для обновления с таким id: " + newFilmForUpdate.getId());
    }

    @Override
    public Film deleteFilm(long idFilmForDelete) {
        if (allFilmsByIds.containsKey(idFilmForDelete)) {
            return allFilmsByIds.remove(idFilmForDelete);
        }
        throw new NotFoundException("Не найдено фильма для удаления с id:" + idFilmForDelete);
    }

    @Override
    public Film addLikeFilm(long idOfFilm, long idOfUser) {
        checkUserIsAdded(idOfUser);
        checkFilmIsAdded(idOfFilm);

        Set<Long> idsOfAllUsersWhoLike = filmStorage.getFilmById(idOfFilm).getIdsOfAllUsersWhoLike();
        idsOfAllUsersWhoLike.add(idOfUser);
        return filmStorage.getFilmById(idOfFilm);
    }

    @Override
    public Film deleteLikeFilm(long idOfFilm, long idOfUser) {
        checkUserIsAdded(idOfUser);
        checkFilmIsAdded(idOfFilm);

        Set<Long> idsOfAllUsersWhoLike = filmStorage.getFilmById(idOfFilm).getIdsOfAllUsersWhoLike();
        idsOfAllUsersWhoLike.remove(idOfUser);
        return filmStorage.getFilmById(idOfFilm);
    }

    private long getNextIdForFilm() {
        return ++idForNewFilm;
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