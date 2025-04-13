package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.inDatabase.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageIntegrationTest {
    private final UserDbStorage userStorage;

    @Test
    public void testAddAndGetUserById() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User savedUser = userStorage.addUser(user);
        User retrievedUser = userStorage.getUserById(savedUser.getId());

        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getId()).isEqualTo(savedUser.getId());
        assertThat(retrievedUser.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setEmail("update@example.com");
        user.setLogin("updateuser");
        user.setName("Update User");
        user.setBirthday(LocalDate.of(1985, 5, 5));

        User savedUser = userStorage.addUser(user);
        savedUser.setName("Updated Name");
        savedUser.setEmail("updated@example.com");

        User updatedUser = userStorage.updateUser(savedUser);
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    public void testAddAndRemoveFriend() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        user1 = userStorage.addUser(user1);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(1991, 2, 2));
        user2 = userStorage.addUser(user2);

        userStorage.makeFriends(user1.getId(), user2.getId());
        assertThat(userStorage.getAllFriends(user1.getId()))
                .extracting(User::getId)
                .contains(user2.getId());

        userStorage.deleteFriends(user1.getId(), user2.getId());
        assertThat(userStorage.getAllFriends(user1.getId()))
                .extracting(User::getId)
                .doesNotContain(user2.getId());
    }

    @Test
    public void testGetCommonFriends() {
        User user1 = new User();
        user1.setEmail("user1@common.com");
        user1.setLogin("user1common");
        user1.setName("User One Common");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        user1 = userStorage.addUser(user1);

        User user2 = new User();
        user2.setEmail("user2@common.com");
        user2.setLogin("user2common");
        user2.setName("User Two Common");
        user2.setBirthday(LocalDate.of(1991, 2, 2));
        user2 = userStorage.addUser(user2);

        User common = new User();
        common.setEmail("common@common.com");
        common.setLogin("commonuser");
        common.setName("Common User");
        common.setBirthday(LocalDate.of(1992, 3, 3));
        common = userStorage.addUser(common);

        userStorage.makeFriends(user1.getId(), common.getId());
        userStorage.makeFriends(user2.getId(), common.getId());

        Collection<User> commonFriends = userStorage.getCommonFriends(user1.getId(), user2.getId());
        assertThat(commonFriends).isNotEmpty();
        assertThat(commonFriends.iterator().next().getId()).isEqualTo(common.getId());
    }
}