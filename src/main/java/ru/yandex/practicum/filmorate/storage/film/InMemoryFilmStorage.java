package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.comparators.FilmComparatorByAmountOfLikes;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Long, Film> allFilmsByIds = new HashMap<>();
    private long idForNewFilm = 0;

    @Override
    public Collection<Film> getAllFilms() {
        return allFilmsByIds.values();
    }

    @Override
    public Film getFilmById(long id) {
        if (allFilmsByIds.containsKey(id)) {
            return allFilmsByIds.get(id);
        }
        throw new NotFoundException(String.format("Фильма для отображения с id=%d не найдено", id));
    }

    @Override
    public Collection<Film> getMostLikedFilms(int count) {
        count = Math.min(count, allFilmsByIds.size());
        return allFilmsByIds.values().stream()
                .sorted(new FilmComparatorByAmountOfLikes())
                .toList()
                .subList(0, count);
    }

    @Override
    public Film addFilm(Film newFilmForAdd) {
        newFilmForAdd.setId(getNextIdForFilm());
        allFilmsByIds.put(newFilmForAdd.getId(), newFilmForAdd);
        log.info("new film added");
        return newFilmForAdd;
    }

    @Override
    public Film updateFilm(Film newFilmForUpdate) {
        if (allFilmsByIds.containsKey(newFilmForUpdate.getId())) {
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

    private long getNextIdForFilm() {
        return ++idForNewFilm;
    }
}