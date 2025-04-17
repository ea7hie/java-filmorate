package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.util.Collection;

@Slf4j
@Service
public class GenreService {
    private final GenreStorage genreStorage;

    public GenreService(@Qualifier("genreDbStorage") GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Collection<Genres> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    public Genres getGenreById(int idOfGenre) {
        Genres genreByIndex = genreStorage.getGenreByIndex(idOfGenre);
        if (genreByIndex == null) {
            throw new NotFoundException(String.format("Жанр с id = %d не найден", idOfGenre));
        }
        return genreByIndex;
    }
}
