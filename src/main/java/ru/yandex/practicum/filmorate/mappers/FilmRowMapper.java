package ru.yandex.practicum.filmorate.mappers;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Long filmId = resultSet.getLong("film_id");
        String title = resultSet.getString("title");
        String description = resultSet.getString("description");
        LocalDate releaseDate = resultSet.getDate("release_date").toLocalDate();
        Integer duration = resultSet.getInt("duration");
        int mpaId = resultSet.getInt("mpa_rating_id");

        String likesSqlQuery = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Long> likes = jdbcTemplate.query(
                likesSqlQuery, (rs1, row1) -> rs1.getLong("user_id"), filmId);
        Set<Long> allLikes = new HashSet<>(likes);

        String genreSql =
                "SELECT g.genre_id, g.genre_name FROM film_genre fg " +
                        "JOIN genres g ON fg.genre_id = g.genre_id " +
                        "WHERE fg.film_id = ?";
        List<Genres> genres = jdbcTemplate.query(
                genreSql, (rs2, row2) ->
                        new Genres(rs2.getInt("genre_id"), rs2.getString("genre_name")), filmId);

        String getMpaSqlQuery = "SELECT mpa_rating_id, description FROM mpa_ratings WHERE mpa_rating_id = ?";
        MpaRatings mpa = jdbcTemplate.queryForObject(getMpaSqlQuery, (rs3, rowNum3) ->
                new MpaRatings(rs3.getInt("mpa_rating_id"), rs3.getString("description")), mpaId);

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