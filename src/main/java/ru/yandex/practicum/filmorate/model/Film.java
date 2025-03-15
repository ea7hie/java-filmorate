package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.yandex.practicum.filmorate.annotations.CheckDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


/**
 * Film.
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
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
}