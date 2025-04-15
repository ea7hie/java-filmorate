package ru.yandex.practicum.filmorate.mappers.films;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.MpaRatings;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {
    private final JdbcTemplate jdbcTemplate;
    private final QueryToDatabaseForFilms query = new QueryToDatabaseForFilms();

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Long filmId = resultSet.getLong("film_id");
        String title = resultSet.getString("title");
        String description = resultSet.getString("description");
        LocalDate releaseDate = resultSet.getDate("release_date").toLocalDate();
        Integer duration = resultSet.getInt("duration");
        int mpaId = resultSet.getInt("mpa_rating_id");

        Set<Long> allLikes = query.getIdsAllUsersWhoLiked(filmId, jdbcTemplate);
        List<Genres> genres = query.getAllGenres(filmId, jdbcTemplate);
        MpaRatings mpa = query.getMpaRating(filmId, mpaId, jdbcTemplate);

        Film film = new Film();
        film.setId(filmId);
        film.setName(title);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        film.setIdsOfAllUsersWhoLike(allLikes);
        film.setGenres(genres);
        film.setMpa(mpa);
        return film;
    }
}