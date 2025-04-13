package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.util.Collection;
import java.util.Optional;

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

    public Optional<Genres> getGenreById(int idOfGenre) {
        return genreStorage.getGenreByIndex(idOfGenre);
    }
}
