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
import java.util.Set;

/**
 * Film.
 */
@Data
@NoArgsConstructor
public class Film {

    private Long id;
    private Set<Long> idsOfAllUsersWhoLike = new HashSet<>();

    @NonNull
    @NotBlank
    private String name;

    @NonNull
    @Size(max = 200)
    private String description;

    @NonNull
    @CheckDate
    private LocalDate releaseDate;

    @Positive
    @NonNull
    private Integer duration;

    Genres[] genres;
    MpaRatings mpaRating;

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