package ru.yandex.practicum.filmorate.comparators;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;

public class FilmComparatorByAmountOfLikes implements Comparator<Film> {
    @Override
    public int compare(Film film1, Film film2) {
        return film1.getIdsOfAllUsersWhoLike().size() - film2.getIdsOfAllUsersWhoLike().size();
    }
}