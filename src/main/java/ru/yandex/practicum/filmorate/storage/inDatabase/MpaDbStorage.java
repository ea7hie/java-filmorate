package ru.yandex.practicum.filmorate.storage.inDatabase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRatings;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository
@Qualifier("mpaDbStorage")
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<MpaRatings> getAllMpaRatings() {
        String sql = "SELECT mpa_rating_id, description FROM mpa_ratings";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new MpaRatings(rs.getInt("mpa_rating_id"), rs.getString("description")));
    }

    @Override
    public Optional<MpaRatings> getMpaRatingByIndex(int id) {
        String sql = "SELECT mpa_rating_id, description FROM mpa_ratings WHERE mpa_rating_id = ?";
        Optional<MpaRatings> mpaRatingsOptional = Optional.empty();
        try {
            mpaRatingsOptional = Optional.ofNullable(jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                    new MpaRatings(rs.getInt("mpa_rating_id"), rs.getString("description")), id));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("MPA рейтинг с id = %d не найден", id));
        }
        return mpaRatingsOptional;
    }
}
