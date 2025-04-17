package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRatings;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

import java.util.Collection;

@Slf4j
@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    public MpaService(@Qualifier("mpaDbStorage") MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Collection<MpaRatings> getAllMpaRatings() {
        return mpaStorage.getAllMpaRatings();
    }

    public MpaRatings getMpaRatingById(int idOfMpaRating) {
        MpaRatings mpaRatingByIndex = mpaStorage.getMpaRatingByIndex(idOfMpaRating);
        if (mpaRatingByIndex == null) {
            throw new NotFoundException(String.format("MPA рейтинг с id = %d не найден", idOfMpaRating));
        }
        return mpaRatingByIndex;
    }
}
