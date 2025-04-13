package ru.yandex.practicum.filmorate.storage.inDatabase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
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
        checkMpaIsAdded(newFilmForAdd.getMpa().getId());

        String addFilmSqlQuery = "INSERT INTO films (mpa_rating_id, title, description, release_date, duration)" +
                "VALUES (?, ?, ?, ?, ?)";

        long newId = insert(addFilmSqlQuery, newFilmForAdd.getMpa().getId(), newFilmForAdd.getName(),
                newFilmForAdd.getDescription(), newFilmForAdd.getReleaseDate(), newFilmForAdd.getDuration());

        newFilmForAdd.setId(newId);

        List<Genres> allGenres = new ArrayList<>();
        try {
            String setGenres = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            Set<Genres> uniqueGenres = new HashSet<>(newFilmForAdd.getGenres());
            for (Genres uniqueGenre : uniqueGenres) {
                allGenres.addLast(uniqueGenre);
                jdbcTemplate.update(setGenres, newFilmForAdd.getId(), uniqueGenre.getId());
            }
        } catch (Exception e) {
            throw new NotFoundException("Не найдено такого жанра");
        }

        newFilmForAdd.setGenres(allGenres);
        log.info("new film added");
        return newFilmForAdd;
    }

    @Override
    public Film updateFilm(@Valid Film newFilmForUpdate) {
        Long filmId = newFilmForUpdate.getId();
        Film filmById = getFilmById(filmId);

        String sql = "UPDATE films SET title=?, description=?, release_date=?, duration=?, mpa_rating_id=? " +
                "WHERE film_id=?";
        jdbcTemplate.update(sql, newFilmForUpdate.getName(), newFilmForUpdate.getDescription(),
                newFilmForUpdate.getReleaseDate(), newFilmForUpdate.getDuration(), newFilmForUpdate.getMpa().getId(),
                filmId);

        String deleteGenresSql = "DELETE FROM film_genre WHERE film_id=?";
        jdbcTemplate.update(deleteGenresSql, filmId);

        List<Genres> allGenres = new ArrayList<>();
        if (newFilmForUpdate.getGenres() != null) {
            String setGenres = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            Set<Genres> uniqueGenres = new HashSet<>(newFilmForUpdate.getGenres());
            for (Genres uniqueGenre : uniqueGenres) {
                allGenres.addLast(uniqueGenre);
                jdbcTemplate.update(setGenres, newFilmForUpdate.getId(), uniqueGenre.getId());
            }
        }

        newFilmForUpdate.setGenres(allGenres);
        log.info("old film updated");
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

    private long insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);

        if (id != null) {
            return id;
        } else {
            throw new ValidationException("Не удалось сохранить данные");
        }
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

    private void checkMpaIsAdded(int mpaId) {
        String sql = "SELECT description FROM mpa_ratings WHERE mpa_rating_id=?";
        try {
            jdbcTemplate.queryForObject(sql, String.class, mpaId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("MPA рейтинг с id = %d не найден", mpaId));
        }
    }
}
