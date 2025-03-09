package ru.yandex.practicum.filmorate.annotations;

import jakarta.validation.Constraint;
import ru.yandex.practicum.filmorate.validators.DateValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = DateValidator.class)
public @interface CheckDate {
    String message() default "Фильм не мог быть снят до 28 декабря 1895г.";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

    String value() default "1895-12-28";
}