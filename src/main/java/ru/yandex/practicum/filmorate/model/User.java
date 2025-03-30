package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class User {

    private Long id;
    private String name;
    private Map<Long, Boolean> idsOfAllFriends = new HashMap<>();

    @NotBlank(message = "email пользователя не может быть пустым")
    @Email(message = "Введённый вами email не соответствует стандартам")
    private String email;

    @NotBlank(message = "Логин пользователя не может быть пустым")
    private String login;

    @NotNull
    @Past(message = "День рождения пользователя не может быть в будущем")
    private LocalDate birthday;

    public User(String name, String email, String login, LocalDate birthday) {
        this.name = name;
        this.email = email;
        this.login = login;
        this.birthday = birthday;
    }

    public User(Long id, String email, String login, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.birthday = birthday;
    }

    public User(String email, String login, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.birthday = birthday;
    }

    public User(Long id, String name, String email, String login, LocalDate birthday) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.login = login;
        this.birthday = birthday;
    }
}