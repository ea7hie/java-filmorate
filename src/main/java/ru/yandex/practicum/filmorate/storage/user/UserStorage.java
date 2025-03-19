package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getAllUsers();

    User getUserById(long id);

    User addUser(User newUserForAdd);

    User updateUser(User newUserForUpdate);

    User deleteUser(long idUserForDelete);
}