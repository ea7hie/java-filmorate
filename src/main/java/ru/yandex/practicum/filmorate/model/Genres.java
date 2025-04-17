package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Genres {
    private int id;
    private String name;

    public Genres(int genreId) {
        this.id = genreId;
    }

    public Genres(int id, String name) {
        this.id = id;
        this.name = name;
    }
}