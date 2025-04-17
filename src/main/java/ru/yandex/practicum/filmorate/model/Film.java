package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.yandex.practicum.filmorate.annotations.CheckDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Film.
 */
@Data
@NoArgsConstructor
public class Film {

    private Long id;
    private Set<Long> idsOfAllUsersWhoLike = new HashSet<>();
    private List<Genres> genres;
    private MpaRatings mpa;

    @NonNull
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @NonNull
    @Size(max = 200, message = "Длина описания фильма не может быть больше 200 символов")
    private String description;

    @NonNull
    @CheckDate(message = "Фильм не мог быть снят до 28 декабря 1895г")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма не может быть отрицательной")
    @NonNull
    private Integer duration;

    public Film(String name,
                String description,
                LocalDate releaseDate,
                Integer duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film(Long id,
                String name,
                String description,
                LocalDate releaseDate,
                Integer duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.id = id;
    }
}