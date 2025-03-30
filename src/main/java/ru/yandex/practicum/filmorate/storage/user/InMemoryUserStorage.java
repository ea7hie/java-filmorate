package ru.yandex.practicum.filmorate.storage.user;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private Map<Long, User> allUsersByIds = new HashMap<>();
    private long idForNewUser = 0;

    @Override
    public Collection<User> getAllUsers() {
        return allUsersByIds.values();
    }

    @Override
    public User getUserById(long id) {
        if (allUsersByIds.containsKey(id)) {
            return allUsersByIds.get(id);
        }
        throw new NotFoundException(String.format("Пользователя для отображения с id=%d не найдено", id));
    }

    @Override
    public User addUser(@Valid User newUserForAdd) {
        if (isEmailAlreadyUsed(newUserForAdd.getEmail())) {
            log.error("new email already used");
            throw new ValidationException("Этот имейл уже используется");
        }
        if (hasLoginSpaces(newUserForAdd.getLogin())) {
            log.error("login has spaces");
            throw new ValidationException("Логин не должен содержать пробелы");
        }
        if (isNameEmpty(newUserForAdd.getName())) {
            log.error("new user without name; name is login");
            newUserForAdd.setName(newUserForAdd.getLogin());
        }
        newUserForAdd.setId(getNextIdForUser());
        allUsersByIds.put(newUserForAdd.getId(), newUserForAdd);
        return newUserForAdd;
    }

    @Override
    public User updateUser(@Valid User newUserForUpdate) {
        if (!allUsersByIds.containsKey(newUserForUpdate.getId())) {
            log.info("not founded user with id {}", newUserForUpdate.getId());
            throw new NotFoundException("Не найдено пользователя для обновления с id:" + newUserForUpdate.getId());
        }
        if (hasLoginSpaces(newUserForUpdate.getLogin())) {
            log.error("new login has spaces");
            throw new ValidationException("Логин не должен содержать пробелы");
        }

        User oldUser = allUsersByIds.get(newUserForUpdate.getId());
        if (!oldUser.getEmail().equals(newUserForUpdate.getEmail())) {
            if (isEmailAlreadyUsed(newUserForUpdate.getEmail())) {
                log.info("new email already used");
                throw new ValidationException("Новый имейл уже используется, используйте другой.");
            }
            oldUser.setEmail(newUserForUpdate.getEmail());
        }

        if (isNameEmpty(newUserForUpdate.getName())) {
            log.info("new user without name; new name is login");
            oldUser.setName(newUserForUpdate.getLogin());
        } else {
            oldUser.setName(newUserForUpdate.getName());
        }

        oldUser.setLogin(newUserForUpdate.getLogin());
        oldUser.setBirthday(newUserForUpdate.getBirthday());
        allUsersByIds.put(newUserForUpdate.getId(), oldUser);
        log.info("old user updated");
        return oldUser;
    }

    @Override
    public User deleteUser(long idUserForDelete) {
        if (allUsersByIds.containsKey(idUserForDelete)) {
            return allUsersByIds.remove(idUserForDelete);
        }
        throw new NotFoundException("Не найдено пользователя для удаления с id:" + idUserForDelete);
    }


    private long getNextIdForUser() {
        return ++idForNewUser;
    }

    private boolean isEmailAlreadyUsed(String emailForCheck) {
        return allUsersByIds.values().stream()
                .map(User::getEmail)
                .toList()
                .contains(emailForCheck);
    }

    private boolean isNameEmpty(String nameForCheck) {
        return nameForCheck == null || nameForCheck.isBlank();
    }

    private boolean hasLoginSpaces(String loginForCheck) {
        return loginForCheck.contains(" ");
    }
}