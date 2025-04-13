package ru.yandex.practicum.filmorate.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class UserRowMapper implements RowMapper<User> {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        long userId = resultSet.getLong("user_id");
        String name = resultSet.getString("name");
        String login = resultSet.getString("login");
        String email = resultSet.getString("email");
        LocalDate birthday = resultSet.getDate("birthday").toLocalDate();

        String friendsSqlQuery = "SELECT user_friend_id, status FROM friends WHERE user_id=?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(friendsSqlQuery, userId);
        Map<Long, Boolean> friends = new HashMap<>();
        for (Map<String, Object> row : rows) {
            long friendId = (Long) row.get("user_friend_id");
            boolean status = (Boolean) row.get("status");
            friends.put(friendId, status);
        }

        User user = new User();
        user.setId(userId);
        user.setName(name);
        user.setLogin(login);
        user.setEmail(email);
        user.setBirthday(birthday);
        user.setIdsOfAllFriends(friends);
        return user;
    }
}