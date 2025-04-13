package ru.yandex.practicum.filmorate.storage.inDatabase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.*;

@Slf4j
@Repository
@Qualifier("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Film> getAllFilms() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, new FilmRowMapper(jdbcTemplate));
    }

    @Override
    public Film getFilmById(long id) {
        String sql = "SELECT * FROM films WHERE film_id=?";
        try {
            return jdbcTemplate.queryForObject(sql, new FilmRowMapper(jdbcTemplate), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Фильма для отображения с id=%d не найдено", id));
        }
    }

    @Override
    public Film addFilm(@Valid Film newFilmForAdd) {
        mbaInDB(newFilmForAdd.getMpa().getId());

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");

        Map<String, Object> parameters = Map.of(
                "title", newFilmForAdd.getName(),
                "description", newFilmForAdd.getDescription(),
                "release_date", newFilmForAdd.getReleaseDate(),
                "duration", newFilmForAdd.getDuration(),
                "mpa_rating_id", newFilmForAdd.getMpa().getId()
        );
        Number newId = insert.executeAndReturnKey(parameters);
        newFilmForAdd.setId(newId.longValue());

        genresToDb(newFilmForAdd);
        log.info("new film added");
        return newFilmForAdd;
    }

    @Override
    public Film updateFilm(@Valid Film newFilmForUpdate) {
        if (newFilmForUpdate.getId() == null) {
            log.error("Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }
        mbaInDB(newFilmForUpdate.getMpa().getId());

        Long filmId = newFilmForUpdate.getId();
        String sql = "UPDATE films SET title=?, description=?, release_date=?, duration=?, mpa_rating_id=? " +
                "WHERE film_id=?";
        int rowsUpdated = jdbcTemplate.update(sql,
                newFilmForUpdate.getName(),
                newFilmForUpdate.getDescription(),
                newFilmForUpdate.getReleaseDate(),
                newFilmForUpdate.getDuration(),
                newFilmForUpdate.getMpa().getId(),
                filmId);
        if (rowsUpdated == 0) {
            throw new NotFoundException(String.format("Фильм с id = %d не найден", filmId));
        }

        String deleteGenresSql = "DELETE FROM film_genre WHERE film_id=?";
        jdbcTemplate.update(deleteGenresSql, filmId);
        genresToDb(newFilmForUpdate);

        return newFilmForUpdate;
    }

    @Override
    public Film deleteFilm(long idFilmForDelete) {
        Film filmForDelete = getFilmById(idFilmForDelete);
        String filmDeleteSqlQuery = "DELETE FROM films WHERE film_id=?";
        jdbcTemplate.update(filmDeleteSqlQuery, idFilmForDelete);
        return filmForDelete;
    }

    @Override
    public List<Film> getMostLikedFilms(int count) {
        String sql =
                "SELECT f.* " +
                        "FROM films f LEFT JOIN likes lf ON f.film_id = lf.film_id " +
                        "GROUP BY f.film_id " +
                        "ORDER BY COUNT(lf.user_id) DESC " +
                        "LIMIT ?";
        return jdbcTemplate.query(sql, new FilmRowMapper(jdbcTemplate), count);
    }

    @Override
    public Film addLikeFilm(long idOfFilm, long idOfUser) {
        checkUserIsAdded(idOfUser);
        checkFilmIsAdded(idOfFilm);

        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?) ";
        jdbcTemplate.update(sql, idOfFilm, idOfUser);

        return getFilmById(idOfFilm);
    }

    @Override
    public Film deleteLikeFilm(long idOfFilm, long idOfUser) {
        checkUserIsAdded(idOfUser);
        checkFilmIsAdded(idOfFilm);

        String sql = "DELETE FROM likes WHERE film_id=? AND user_id=?";
        jdbcTemplate.update(sql, idOfFilm, idOfUser);

        return getFilmById(idOfFilm);
    }

    private void checkUserIsAdded(long idOfUserForCheck) {
        String getUser = "SELECT user_id FROM users WHERE user_id = ?";
        Long id = jdbcTemplate.queryForObject(getUser, Long.class, idOfUserForCheck);
        if (id == null) {
            throw new NotFoundException(String.format("Пользователя для с id=%d не найдено", idOfUserForCheck));
        }
    }

    private void checkFilmIsAdded(long idOfFilmForCheck) {
        String getFilm = "SELECT film_id FROM films WHERE film_id = ?";
        Long id = jdbcTemplate.queryForObject(getFilm, Long.class, idOfFilmForCheck);
        if (id == null) {
            throw new NotFoundException(String.format("Фильма для с id=%d не найдено", idOfFilmForCheck));
        }
    }

    private void genresToDb(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<Genres> uniqueGenres = new HashSet<>(film.getGenres());

            String genreInsertSql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            String checkGenreInDbSql = "SELECT genre_name FROM genres WHERE genre_id=?";
            for (Genres genre : uniqueGenres) {
                try {
                    jdbcTemplate.queryForObject(checkGenreInDbSql, String.class, genre.getId());
                    jdbcTemplate.update(genreInsertSql, film.getId(), genre.getId());
                } catch (EmptyResultDataAccessException e) {
                    throw new NotFoundException(String.format("Жанр с id = %d не найден", genre.getId()));
                }
            }
        }
    }

    private void mbaInDB(int mpaId) {
        String sql = "SELECT description FROM mpa_ratings WHERE mpa_rating_id=?";
        try {
            jdbcTemplate.queryForObject(sql, String.class, mpaId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("MPA рейтинг с id = %d не найден", mpaId));
        }
    }
}