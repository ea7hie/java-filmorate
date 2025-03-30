package ru.yandex.practicum.filmorate.storage.user;

import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getAllUsers();

    User getUserById(long id);

    User addUser(@Valid User newUserForAdd);

    User updateUser(@Valid User newUserForUpdate);

    User deleteUser(long idUserForDelete);
}