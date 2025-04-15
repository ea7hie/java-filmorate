package ru.yandex.practicum.filmorate.mappers.users;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class QueryToDatabaseForUsers {
    Map<Long, Boolean> getAllFriends(Long userId, JdbcTemplate jdbcTemplate) {
        String friendsSqlQuery = "SELECT user_friend_id, status FROM friends WHERE user_id=?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(friendsSqlQuery, userId);
        Map<Long, Boolean> friends = new HashMap<>();
        for (Map<String, Object> row : rows) {
            long friendId = (Long) row.get("user_friend_id");
            boolean status = (Boolean) row.get("status");
            friends.put(friendId, status);
        }
        return friends;
    }
}