package ru.yandex.practicum.filmorate.storage.inMemory;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
@Primary
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
        checkUserIsAdded(newUserForUpdate.getId());

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
        checkUserIsAdded(idUserForDelete);
        deleteFromFriendsDeletedUser(idUserForDelete);
        return allUsersByIds.remove(idUserForDelete);

    }

    @Override
    public List<User> getAllFriends(long idOfUser) {
        checkUserIsAdded(idOfUser);
        return getUserById(idOfUser).getIdsOfAllFriends().keySet().stream()
                .map(this::getUserById)
                .toList();
    }

    @Override
    public List<User> makeFriends(long idOfUser1, long idOfUser2) {
        checkUserIsAdded(idOfUser1);
        checkUserIsAdded(idOfUser2);

        Map<Long, Boolean> allFriendsOfUser1 = getUserById(idOfUser1).getIdsOfAllFriends();
        Map<Long, Boolean> allFriendsOfUser2 = getUserById(idOfUser2).getIdsOfAllFriends();

        if (allFriendsOfUser1.getOrDefault(idOfUser2, false)
                && allFriendsOfUser2.getOrDefault(idOfUser1, false)) {
            log.info("{} and {} are already friends.", idOfUser1, idOfUser2);
        } else if (!allFriendsOfUser1.containsKey(idOfUser2) && allFriendsOfUser2.containsKey(idOfUser1)) {
            log.info("{} responds to an incoming friend request from {}.", idOfUser1, idOfUser2);
            allFriendsOfUser1.put(idOfUser2, true);
            allFriendsOfUser2.put(idOfUser1, true);
        } else if (!allFriendsOfUser1.containsKey(idOfUser2)) {
            log.info("{} sent a request to friends to {}.", idOfUser1, idOfUser2);
            allFriendsOfUser1.put(idOfUser2, false);
        } else {
            log.error("Ошибка при попытке добавления в друзья");
            throw new ValidationException("Произошла неизвестная ошибка");
        }

        return List.of(getUserById(idOfUser1), getUserById(idOfUser2));
    }

    @Override
    public List<User> deleteFriends(long idOfUser1, long idOfUser2) {
        checkUserIsAdded(idOfUser1);
        checkUserIsAdded(idOfUser2);

        Map<Long, Boolean> allFriendsOfUser1 = getUserById(idOfUser1).getIdsOfAllFriends();
        Map<Long, Boolean> allFriendsOfUser2 = getUserById(idOfUser2).getIdsOfAllFriends();

        if (allFriendsOfUser1.getOrDefault(idOfUser2, false)
                && allFriendsOfUser2.getOrDefault(idOfUser1, false)) {
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

        return List.of(getUserById(idOfUser1), getUserById(idOfUser2));
    }

    @Override
    public List<User> getCommonFriends(long idOfUser1, long idOfUser2) {
        checkUserIsAdded(idOfUser1);
        checkUserIsAdded(idOfUser2);

        Map<Long, Boolean> allRequestsOfUser1 = getUserById(idOfUser1).getIdsOfAllFriends();
        List<Long> idsOfAllFriendsOfUser1 = new ArrayList<>();
        for (Long id : allRequestsOfUser1.keySet()) {
            if (allRequestsOfUser1.get(id)) {
                idsOfAllFriendsOfUser1.add(id);
            }
        }

        Map<Long, Boolean> allRequestsOfUser2 = getUserById(idOfUser1).getIdsOfAllFriends();
        List<Long> idsOfAllFriendsOfUser2 = new ArrayList<>();
        for (Long id : allRequestsOfUser2.keySet()) {
            if (allRequestsOfUser2.get(id)) {
                idsOfAllFriendsOfUser2.add(id);
            }
        }

        return idsOfAllFriendsOfUser1.stream()
                .filter(idsOfAllFriendsOfUser2::contains)
                .map(this::getUserById)
                .toList();
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

    private void deleteFromFriendsDeletedUser(long idDeletedUser) {
        getAllUsers().stream()
                .filter(user -> user.getIdsOfAllFriends().containsKey(idDeletedUser))
                .map(user -> user.getIdsOfAllFriends().remove(idDeletedUser))
                .close();
    }

    private void checkUserIsAdded(long idOfUserForCheck) {
        if (getUserById(idOfUserForCheck) == null) {
            throw new NotFoundException(String.format("Пользователя для с id=%d не найдено", idOfUserForCheck));
        }
    }
}