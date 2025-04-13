package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.MpaRatings;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.inDatabase.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.inDatabase.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, UserDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageIntegrationTest {

    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    @Test
    public void testAddAndGetFilmById() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(new MpaRatings(1));
        film.setGenres(List.of(new Genres(1)));

        Film savedFilm = filmStorage.addFilm(film);
        Film retrievedFilm = filmStorage.getFilmById(savedFilm.getId());

        assertThat(retrievedFilm).isNotNull();
        assertThat(retrievedFilm.getName()).isEqualTo("Test Film");
    }

    @Test
    public void testUpdateFilm() {
        Film film = new Film();
        film.setName("Original Film");
        film.setDescription("Original description");
        film.setReleaseDate(LocalDate.of(2001, 1, 1));
        film.setDuration(100);
        film.setMpa(new MpaRatings(1));
        film.setGenres(List.of(new Genres(1)));

        Film savedFilm = filmStorage.addFilm(film);
        savedFilm.setName("Updated Film");
        savedFilm.setDescription("Updated description");
        savedFilm.setDuration(150);
        film.setGenres(List.of(new Genres(2)));

        Film updatedFilm = filmStorage.updateFilm(savedFilm);
        assertThat(updatedFilm.getName()).isEqualTo("Updated Film");
        assertThat(updatedFilm.getDuration()).isEqualTo(150);
        assertThat(updatedFilm.getGenres()).containsExactlyInAnyOrder(new Genres(2));
    }

    @Test
    public void testAddAndDeleteLike() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User savedUser = userStorage.addUser(user);

        Film film = new Film();
        film.setName("Film with Likes");
        film.setDescription("Test film");
        film.setReleaseDate(LocalDate.of(2003, 3, 3));
        film.setDuration(130);
        film.setMpa(new MpaRatings(1));
        film.setGenres(List.of(new Genres(1)));
        Film savedFilm = filmStorage.addFilm(film);
        Long filmId = savedFilm.getId();

        Long userId = savedUser.getId();
        filmStorage.addLikeFilm(filmId, userId);
        Collection<Film> popularFilms = filmStorage.getMostLikedFilms(10);
        assertThat(popularFilms).isNotEmpty();

        filmStorage.deleteLikeFilm(filmId, userId);
        Collection<Film> updatedPopularFilms = filmStorage.getMostLikedFilms(10);
        assertThat(updatedPopularFilms).isNotNull();
    }
}