package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.storage.inDatabase.GenreDbStorage;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({GenreDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageIntegrationTest {

    private final GenreDbStorage genreStorage;

    @Test
    public void testGetAllGenres() {
        Collection<Genres> genres = genreStorage.getAllGenres();
        assertThat(genres).isNotEmpty();
    }

    @Test
    public void testGetGenreById() {
        Genres genre = genreStorage.getGenreByIndex(1).get();
        assertThat(genre).isNotNull();
        assertThat(genre.getId()).isEqualTo(1);
    }
}