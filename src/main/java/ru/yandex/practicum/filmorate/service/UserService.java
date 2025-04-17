package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllFriends(long idOfUser) {
        return userStorage.getAllFriends(idOfUser);
    }

    public List<User> makeFriends(long idOfUser1, long idOfUser2) {
        return userStorage.makeFriends(idOfUser1, idOfUser2);
    }

    public List<User> deleteFriends(long idOfUser1, long idOfUser2) {
        return userStorage.deleteFriends(idOfUser1, idOfUser2);
    }

    public List<User> getCommonFriends(long idOfUser1, long idOfUser2) {
        return userStorage.getCommonFriends(idOfUser1, idOfUser2);
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

    public User deleteUser(long idForDelete) {
        return userStorage.deleteUser(idForDelete);
    }
}