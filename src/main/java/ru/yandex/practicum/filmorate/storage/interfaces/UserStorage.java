package ru.yandex.practicum.filmorate.storage.interfaces;

import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {
    Collection<User> getAllUsers();

    User getUserById(long id);

    User addUser(@Valid User newUserForAdd);

    User updateUser(@Valid User newUserForUpdate);

    User deleteUser(long idUserForDelete);

    List<User> getAllFriends(long idOfUser);

    List<User> makeFriends(long idOfUser1, long idOfUser2);

    List<User> deleteFriends(long idOfUser1, long idOfUser2);

    List<User> getCommonFriends(long idOfUser1, long idOfUser2);
}