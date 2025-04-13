package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class MpaRatings {
    private int id;
    private String name;

    public MpaRatings(int id) {
        this.id = id;
    }

    public MpaRatings(int id, String name) {
        this.id = id;
        this.name = name;
    }
}