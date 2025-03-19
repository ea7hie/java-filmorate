package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<User> getAllFriends(long idOfUser) {
        checkUserIsAdded(idOfUser);

        return userStorage.getUserById(idOfUser).getIdsOfAllFriends().stream()
                .map(userStorage::getUserById)
                .toList();
    }

    public List<User> makeFriends(long idOfUser1, long idOfUser2) {
        checkUserIsAdded(idOfUser1);
        checkUserIsAdded(idOfUser2);

        Set<Long> idsOfAllFriendsOfUser1 = userStorage.getUserById(idOfUser1).getIdsOfAllFriends();
        Set<Long> idsOfAllFriendsOfUser2 = userStorage.getUserById(idOfUser2).getIdsOfAllFriends();

        idsOfAllFriendsOfUser1.add(idOfUser2);
        idsOfAllFriendsOfUser2.add(idOfUser1);

        return List.of(userStorage.getUserById(idOfUser1), userStorage.getUserById(idOfUser2));
    }

    public List<User> deleteFriends(long idOfUser1, long idOfUser2) {
        checkUserIsAdded(idOfUser1);
        checkUserIsAdded(idOfUser2);

        Set<Long> idsOfAllFriendsOfUser1 = userStorage.getUserById(idOfUser1).getIdsOfAllFriends();
        Set<Long> idsOfAllFriendsOfUser2 = userStorage.getUserById(idOfUser2).getIdsOfAllFriends();

        idsOfAllFriendsOfUser1.remove(idOfUser2);
        idsOfAllFriendsOfUser2.remove(idOfUser1);

        return List.of(userStorage.getUserById(idOfUser1), userStorage.getUserById(idOfUser2));
    }

    public List<User> getCommonFriends(long idOfUser1, long idOfUser2) {
        checkUserIsAdded(idOfUser1);
        checkUserIsAdded(idOfUser2);

        Set<Long> idsOfAllFriendsOfUser1 = userStorage.getUserById(idOfUser1).getIdsOfAllFriends();
        Set<Long> idsOfAllFriendsOfUser2 = userStorage.getUserById(idOfUser2).getIdsOfAllFriends();

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