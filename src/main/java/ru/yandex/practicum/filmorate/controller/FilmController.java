package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.getFilmStorage().getAllFilms();
    }

    @GetMapping("/{idOfFilm}")
    public Film getFilmById(@PathVariable long idOfFilm) {
        return filmService.getFilmStorage().getFilmById(idOfFilm);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") int amount) {
        return filmService.getFilmStorage().getMostLikedFilms(amount);
    }

    @PostMapping
    public Film addNewFilm(@Valid @RequestBody Film newFilm) {
        return filmService.getFilmStorage().addFilm(newFilm);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        return filmService.getFilmStorage().updateFilm(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film likeFilm(@PathVariable long id,
                         @PathVariable long userId) {
        return filmService.addLikeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film unlikeFilm(@PathVariable long id,
                           @PathVariable long userId) {
        return filmService.deleteLikeFilm(id, userId);
    }
}