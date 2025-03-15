package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    public Collection<User> getAllUsers();

    public User getUserById(long id);

    public User addUser(User newUserForAdd);

    public User updateUser(User newUserForUpdate);

    public User deleteUser(long idUserForDelete);
}