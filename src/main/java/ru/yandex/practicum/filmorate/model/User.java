package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long id;
    private String name;

    @NonNull
    @NotNull
    @NotEmpty
    @NotBlank
    @Email
    private String email;

    @NonNull
    @NotNull
    @NotEmpty
    @NotBlank
    private String login;

    @NonNull
    @Past
    private LocalDate birthday;

    public User(String name,
                @Email @NonNull @NotNull @NotBlank @NotEmpty String email,
                @NonNull @NotNull @NotEmpty @NotBlank String login,
                @Past @NonNull @NotNull @NotEmpty @NotBlank LocalDate birthday) {
        this.name = name;
        this.email = email;
        this.login = login;
        this.birthday = birthday;
    }

    public User(
            Long id,
            @Email @NonNull @NotNull @NotBlank @NotEmpty String email,
            @NonNull @NotNull @NotEmpty @NotBlank String login,
            @Past @NonNull @NotNull @NotEmpty @NotBlank LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.birthday = birthday;
    }
}