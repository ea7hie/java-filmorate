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
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@Qualifier("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, new UserRowMapper(jdbcTemplate));
    }

    @Override
    public User getUserById(long id) {
        String sql = "SELECT * FROM users WHERE user_id=?";
        try {
            User user = jdbcTemplate.queryForObject(sql, new UserRowMapper(jdbcTemplate), id);
            if (user != null) {
                user.setIdsOfAllFriends(getMapOfAllFriends(id));
            }
            return jdbcTemplate.queryForObject(sql, new UserRowMapper(jdbcTemplate), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Пользователя для отображения с id=%d не найдено", id));
        }
    }

    @Override
    public User addUser(@Valid User newUserForAdd) {
        if (hasLoginSpaces(newUserForAdd.getLogin())) {
            log.error("login has spaces");
            throw new ValidationException("Логин не должен содержать пробелы");
        }
        if (isNameEmpty(newUserForAdd.getName())) {
            log.error("new user without name; name is login");
            newUserForAdd.setName(newUserForAdd.getLogin());
        }

        String addUserSqlQuery = "INSERT INTO users (EMAIL, LOGIN, NAME, BIRTHDAY)" +
                "VALUES (?, ?, ?, ?)";

        long newId = insert(addUserSqlQuery, newUserForAdd.getEmail(), newUserForAdd.getLogin(),
                newUserForAdd.getName(), newUserForAdd.getBirthday());

        newUserForAdd.setId(newId);
        log.info("new user added");
        return newUserForAdd;
    }

    @Override
    public User updateUser(@Valid User newUserForUpdate) {
        User oldUser = getUserById(newUserForUpdate.getId());

        if (hasLoginSpaces(newUserForUpdate.getLogin())) {
            log.error("new login has spaces");
            throw new ValidationException("Логин не должен содержать пробелы");
        }
        if (isNameEmpty(newUserForUpdate.getName())) {
            log.info("new user without name; new name is login");
            oldUser.setName(newUserForUpdate.getLogin());
        } else {
            oldUser.setName(newUserForUpdate.getName());
        }

        oldUser.setEmail(newUserForUpdate.getEmail());
        oldUser.setLogin(newUserForUpdate.getLogin());
        oldUser.setBirthday(newUserForUpdate.getBirthday());

        String userUpdateSqlQuery = "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE user_id=?";
        jdbcTemplate.update(userUpdateSqlQuery,
                oldUser.getEmail(),
                oldUser.getLogin(),
                oldUser.getName(),
                oldUser.getBirthday(),
                oldUser.getId());

        log.info("old user updated");
        return oldUser;
    }

    @Override
    public User deleteUser(long idUserForDelete) {
        User userForDelete = getUserById(idUserForDelete);
        String userDeleteSqlQuery = "DELETE FROM users WHERE user_id=?";
        jdbcTemplate.update(userDeleteSqlQuery, idUserForDelete);
        return userForDelete;
    }

    @Override
    public List<User> makeFriends(long idOfUser1, long idOfUser2) {
        User user1 = getUserById(idOfUser1);
        User user2 = getUserById(idOfUser2);

        Map<Long, Boolean> allFriendsOfUser1 = getMapOfAllFriends(idOfUser1);
        Map<Long, Boolean> allFriendsOfUser2 = getMapOfAllFriends(idOfUser2);

        if (allFriendsOfUser1.getOrDefault(idOfUser2, false)
                && allFriendsOfUser2.getOrDefault(idOfUser1, false)) {
            log.info("{} and {} are already friends.", idOfUser1, idOfUser2);
        } else if (!allFriendsOfUser1.containsKey(idOfUser2) && allFriendsOfUser2.containsKey(idOfUser1)) {
            log.info("{} responds to an incoming friend request from {}.", idOfUser1, idOfUser2);
            String makeFriendSql = "UPDATE friends SET status=true WHERE user_id=? AND user_friend_id=?";
            jdbcTemplate.update(makeFriendSql, idOfUser1, idOfUser2);
            jdbcTemplate.update(makeFriendSql, idOfUser2, idOfUser1);
        } else if (!allFriendsOfUser1.containsKey(idOfUser2)) {
            log.info("{} sent a request to friends to {}.", idOfUser1, idOfUser2);
            String makeSentRequest = "INSERT INTO friends (user_id, user_friend_id, status) VALUES (?, ?, false)";
            jdbcTemplate.update(makeSentRequest, idOfUser1, idOfUser2);
        } else {
            log.error("Ошибка при попытке добавления в друзья");
            throw new ValidationException("Произошла неизвестная ошибка");
        }

        return List.of(getUserById(idOfUser1), getUserById(idOfUser2));
    }

    @Override
    public List<User> getAllFriends(long idOfUser) {
        User user = getUserById(idOfUser);
        String sql = "SELECT user_friend_id FROM friends WHERE user_id=?";
        return jdbcTemplate.queryForList(sql, Long.class, idOfUser).stream()
                .map(this::getUserById)
                .toList();
    }

    @Override
    public List<User> deleteFriends(long idOfUser1, long idOfUser2) {
        User user1 = getUserById(idOfUser1);
        User user2 = getUserById(idOfUser2);

        Map<Long, Boolean> allFriendsOfUser1 = user1.getIdsOfAllFriends();
        Map<Long, Boolean> allFriendsOfUser2 = user2.getIdsOfAllFriends();

        String deleteFromFriends = "DELETE FROM friends WHERE user_id=? AND user_friend_id=?";
        String setFalse = "UPDATE friends SET status=false WHERE user_id=? AND user_friend_id=?";

        if (allFriendsOfUser1.getOrDefault(idOfUser2, false)
                && allFriendsOfUser2.getOrDefault(idOfUser1, false)) {
            log.info("{} removed {} from friends.", idOfUser1, idOfUser2);
            jdbcTemplate.update(deleteFromFriends, idOfUser1, idOfUser2);
            jdbcTemplate.update(setFalse, idOfUser2, idOfUser1);
        } else if (!allFriendsOfUser1.containsKey(idOfUser2)) {
            log.info("{} didn't send a request to friends to {}.", idOfUser1, idOfUser2);
        } else if (allFriendsOfUser1.containsKey(idOfUser2)) {
            log.info("{} removed request to friends to {}.", idOfUser1, idOfUser2);
            jdbcTemplate.update(deleteFromFriends, idOfUser1, idOfUser2);
        } else {
            log.error("Ошибка при попытке удаления из друзей");
            throw new jakarta.validation.ValidationException("Произошла неизвестная ошибка");
        }

        return List.of(getUserById(idOfUser1), getUserById(idOfUser2));
    }

    @Override
    public List<User> getCommonFriends(long idOfUser1, long idOfUser2) {
        User user1 = getUserById(idOfUser1);
        User user2 = getUserById(idOfUser2);

        String sql =
                "SELECT u.* " +
                        "FROM friends f1 " +
                        "JOIN friends f2 ON f1.user_friend_id = f2.user_friend_id " +
                        "JOIN users u ON f1.user_friend_id = u.user_id " +
                        "WHERE f1.user_id = ? AND f2.user_id = ?";
        return jdbcTemplate.query(sql, new UserRowMapper(jdbcTemplate), idOfUser1, idOfUser2);
    }

    private boolean isNameEmpty(String nameForCheck) {
        return nameForCheck == null || nameForCheck.isBlank();
    }

    private boolean hasLoginSpaces(String loginForCheck) {
        return loginForCheck.contains(" ");
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

    private Map<Long, Boolean> getMapOfAllFriends(long idOfUser) {
        String sql = "SELECT user_friend_id FROM friends WHERE user_id=?";
        String getStatus = "SELECT status FROM friends WHERE user_id=? AND user_friend_id=?";

        List<Long> ids = jdbcTemplate.queryForList(sql, Long.class, idOfUser);
        Map<Long, Boolean> friends = new HashMap<>();

        for (Long id : ids) {
            Boolean isFriends = jdbcTemplate.queryForObject(getStatus, Boolean.class, idOfUser, id);
            friends.put(id, isFriends);
        }
        jdbcTemplate.queryForList(sql, Long.class, idOfUser);

        return friends;
    }
}