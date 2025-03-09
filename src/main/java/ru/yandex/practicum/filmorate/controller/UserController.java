package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private Map<Long, User> allUsersByIds = new HashMap<>();
    private long idForNewUser = 0;

    @GetMapping
    public Collection<User> getAllUsers() {
        return allUsersByIds.values();
    }

    @PostMapping
    public User addNewUser(@Valid @RequestBody User newUser) {
        newUser.setId(getNextIdForUser());
        if (isNameEmpty(newUser.getName())) {
            log.info("new user without name; name is login");
            newUser.setName(newUser.getLogin());
        }
        if (hasLoginSpaces(newUser.getLogin())) {
            log.error("login has spaces");
            throw new ValidationException("Логин не может содержать пробелы");
        }
        allUsersByIds.put(newUser.getId(), newUser);
        return newUser;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        if (!allUsersByIds.containsKey(newUser.getId())) {
            log.info("not founded user with id {}", newUser.getId());
            throw new ValidationException("Не найдено пользователя с таким id: " + newUser.getId());
        }

        if (hasLoginSpaces(newUser.getLogin())) {
            log.error("new login has spaces");
            throw new ValidationException("Логин не может содержать пробелы");
        }

        User oldUser = allUsersByIds.get(newUser.getId());
        if (!oldUser.getEmail().equals(newUser.getEmail())) {
            if (isEmailAlreadyUsed(newUser.getEmail())) {
                log.info("new email already used");
                throw new ValidationException("Этот имейл уже используется");
            }
            oldUser.setEmail(newUser.getEmail());
        }

        if (isNameEmpty(newUser.getName())) {
            log.info("new user without name; new name is login");
            newUser.setName(newUser.getLogin());
        }

        allUsersByIds.put(newUser.getId(), newUser);
        log.info("old user updated");
        return newUser;
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
        return nameForCheck == null || nameForCheck.isBlank() || nameForCheck.isEmpty();
    }

    private boolean hasLoginSpaces(String loginForCheck) {
        return loginForCheck.contains(" ");
    }
}