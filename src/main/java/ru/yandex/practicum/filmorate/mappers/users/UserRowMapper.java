package ru.yandex.practicum.filmorate.mappers.users;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;

@RequiredArgsConstructor
public class UserRowMapper implements RowMapper<User> {
    private final JdbcTemplate jdbcTemplate;
    private final QueryToDatabaseForUsers query = new QueryToDatabaseForUsers();

    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        long userId = resultSet.getLong("user_id");
        String name = resultSet.getString("name");
        String login = resultSet.getString("login");
        String email = resultSet.getString("email");
        LocalDate birthday = resultSet.getDate("birthday").toLocalDate();

        Map<Long, Boolean> friends = query.getAllFriends(userId, jdbcTemplate);

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