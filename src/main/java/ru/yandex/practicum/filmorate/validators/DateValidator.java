package ru.yandex.practicum.filmorate.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotations.CheckDate;

import java.time.LocalDate;

public class DateValidator implements ConstraintValidator<CheckDate, LocalDate> {
    private LocalDate dateOfStart;

    @Override
    public void initialize(CheckDate constraintAnnotation) {
        dateOfStart = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return localDate.isEqual(dateOfStart) || localDate.isAfter(dateOfStart);
    }
}