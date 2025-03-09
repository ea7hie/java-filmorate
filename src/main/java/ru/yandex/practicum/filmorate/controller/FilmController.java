package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private Map<Long, Film> allFilmsByIds = new HashMap<>();
    private long idForNewFilm = 0;

    @GetMapping
    public Collection<Film> getAllFilms() {
        return allFilmsByIds.values();
    }

    @PostMapping
    public Film addNewFilm(@Valid @RequestBody Film newFilm) {
        newFilm.setId(getNextIdForFilm());
        allFilmsByIds.put(newFilm.getId(), newFilm);
        log.info("new film added");
        return newFilm;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        if (allFilmsByIds.containsKey(newFilm.getId())) {
            allFilmsByIds.put(newFilm.getId(), newFilm);
            log.info("old film updated");
            return newFilm;
        }
        log.info("not founded the film by id {}", newFilm.getId());
        throw new ValidationException("Не найдено фильма с таким id: " + newFilm.getId());
    }

    private long getNextIdForFilm() {
        return ++idForNewFilm;
    }

    private boolean isNull(Object o) {
        return o == null;
    }
}