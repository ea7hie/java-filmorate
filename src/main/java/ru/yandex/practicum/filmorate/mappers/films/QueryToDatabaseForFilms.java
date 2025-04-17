package ru.yandex.practicum.filmorate.mappers.films;

import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.MpaRatings;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class QueryToDatabaseForFilms {
    Set<Long> getIdsAllUsersWhoLiked(Long filmId, JdbcTemplate jdbcTemplate) {
        String likesSqlQuery = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Long> likes = jdbcTemplate.query(
                likesSqlQuery, (rs1, row1) -> rs1.getLong("user_id"), filmId);
        return new HashSet<>(likes);
    }

    List<Genres> getAllGenres(Long filmId, JdbcTemplate jdbcTemplate) {
        String genreSql =
                "SELECT g.genre_id, g.genre_name FROM film_genre fg " +
                        "JOIN genres g ON fg.genre_id = g.genre_id " +
                        "WHERE fg.film_id = ?" +
                        "ORDER BY genre_id";
        return jdbcTemplate.query(genreSql, (rs2, row2) ->
                new Genres(rs2.getInt("genre_id"), rs2.getString("genre_name")), filmId);
    }

    MpaRatings getMpaRating(Long filmId, int mpaId, JdbcTemplate jdbcTemplate) {
        String getMpaSqlQuery = "SELECT mpa_rating_id, description FROM mpa_ratings WHERE mpa_rating_id = ?";
        return jdbcTemplate.queryForObject(getMpaSqlQuery, (rs3, rowNum3) ->
                new MpaRatings(rs3.getInt("mpa_rating_id"), rs3.getString("description")), mpaId);
    }
}