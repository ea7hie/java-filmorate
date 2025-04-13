package ru.yandex.practicum.filmorate.storage.inDatabase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository
@Qualifier("genreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Genres> getAllGenres() {
        String sql = "SELECT genre_id, genre_name FROM genres";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Genres(rs.getInt("genre_id"), rs.getString("genre_name")));
    }

    @Override
    public Optional<Genres> getGenreByIndex(int id) {
        String sql = "SELECT genre_id, genre_name FROM genres WHERE genre_id = ?";
        Optional<Genres> genresOptional = Optional.empty();
        try {
            genresOptional = Optional.ofNullable(jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                    new Genres(rs.getInt("genre_id"), rs.getString("genre_name")), id));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Жанр с id = %d не найден", id));
        }
        return genresOptional;
    }
}
