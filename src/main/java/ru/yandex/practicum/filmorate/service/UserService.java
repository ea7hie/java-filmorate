package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<User> getAllFriends(long idOfUser) {
        checkUserIsAdded(idOfUser);

        return userStorage.getUserById(idOfUser).getIdsOfAllFriends().keySet().stream()
                .map(userStorage::getUserById)
                .toList();
    }

    public List<User> makeFriends(long idOfUser1, long idOfUser2) {
        checkUserIsAdded(idOfUser1);
        checkUserIsAdded(idOfUser2);

        Map<Long, Boolean> allFriendsOfUser1 = userStorage.getUserById(idOfUser1).getIdsOfAllFriends();
        Map<Long, Boolean> allFriendsOfUser2 = userStorage.getUserById(idOfUser2).getIdsOfAllFriends();

        if (allFriendsOfUser1.get(idOfUser2) && allFriendsOfUser2.get(idOfUser1)) {
            log.info("{} and {} are already friends.", idOfUser1, idOfUser2);
        } else if (!allFriendsOfUser1.containsKey(idOfUser2)) {
            log.info("{} sent a request to friends to {}.", idOfUser1, idOfUser2);
            allFriendsOfUser1.put(idOfUser2, false);
        } else if (!allFriendsOfUser1.containsKey(idOfUser2) && allFriendsOfUser2.containsKey(idOfUser1)) {
            log.info("{} responds to an incoming friend request from {}.", idOfUser1, idOfUser2);
            allFriendsOfUser1.put(idOfUser2, true);
            allFriendsOfUser2.put(idOfUser1, true);
        } else {
            log.error("Ошибка при попытке добавления в друзья");
            throw new ValidationException("Произошла неизвестная ошибка");
        }

        return List.of(userStorage.getUserById(idOfUser1), userStorage.getUserById(idOfUser2));
    }

    public List<User> deleteFriends(long idOfUser1, long idOfUser2) {
        checkUserIsAdded(idOfUser1);
        checkUserIsAdded(idOfUser2);

        Map<Long, Boolean> allFriendsOfUser1 = userStorage.getUserById(idOfUser1).getIdsOfAllFriends();
        Map<Long, Boolean> allFriendsOfUser2 = userStorage.getUserById(idOfUser2).getIdsOfAllFriends();

        if (allFriendsOfUser1.get(idOfUser2) && allFriendsOfUser2.get(idOfUser1)) {
            log.info("{} removed {} from friends.", idOfUser1, idOfUser2);
            allFriendsOfUser1.remove(idOfUser2);
            allFriendsOfUser2.put(idOfUser1, false);
        } else if (!allFriendsOfUser1.containsKey(idOfUser2)) {
            log.info("{} didn't send a request to friends to {}.", idOfUser1, idOfUser2);
            throw new NotFoundException("Заявка в друзья этому пользователю не отправлялась.");
        } else if (allFriendsOfUser1.containsKey(idOfUser2)) {
            log.info("{} removed request to friends to {}.", idOfUser1, idOfUser2);
            allFriendsOfUser1.remove(idOfUser2);
        } else {
            log.error("Ошибка при попытке удаления из друзей");
            throw new ValidationException("Произошла неизвестная ошибка");
        }

        return List.of(userStorage.getUserById(idOfUser1), userStorage.getUserById(idOfUser2));
    }

    public List<User> getCommonFriends(long idOfUser1, long idOfUser2) {
        checkUserIsAdded(idOfUser1);
        checkUserIsAdded(idOfUser2);

        Map<Long, Boolean> allRequestsOfUser1 = userStorage.getUserById(idOfUser1).getIdsOfAllFriends();
        List<Long> idsOfAllFriendsOfUser1 = new ArrayList<>();
        for (Long id : allRequestsOfUser1.keySet()) {
            if (allRequestsOfUser1.get(id)) {
                idsOfAllFriendsOfUser1.add(id);
            }
        }

        Map<Long, Boolean> allRequestsOfUser2 = userStorage.getUserById(idOfUser1).getIdsOfAllFriends();
        List<Long> idsOfAllFriendsOfUser2 = new ArrayList<>();
        for (Long id : allRequestsOfUser2.keySet()) {
            if (allRequestsOfUser2.get(id)) {
                idsOfAllFriendsOfUser2.add(id);
            }
        }

        return idsOfAllFriendsOfUser1.stream()
                .filter(idsOfAllFriendsOfUser2::contains)
                .map(userStorage::getUserById)
                .toList();
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(long idOfUser) {
        return userStorage.getUserById(idOfUser);
    }

    public User addNewUser(User newUser) {
        return userStorage.addUser(newUser);
    }

    public User updateUser(User newUser) {
        return userStorage.updateUser(newUser);
    }

    private void checkUserIsAdded(long idOfUserForCheck) {
        if (userStorage.getUserById(idOfUserForCheck) == null) {
            throw new NotFoundException(String.format("Пользователя для с id=%d не найдено", idOfUserForCheck));
        }
    }
}