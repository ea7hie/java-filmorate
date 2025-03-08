package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;
import ru.yandex.practicum.filmorate.annotations.CheckDate;

import java.time.LocalDate;


/**
 * Film.
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Film {

    private Long id;

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