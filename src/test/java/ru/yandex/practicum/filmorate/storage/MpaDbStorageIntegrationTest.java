package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.MpaRatings;
import ru.yandex.practicum.filmorate.storage.inDatabase.MpaDbStorage;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({MpaDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageIntegrationTest {
    private final MpaDbStorage mpaStorage;

    @Test
    public void testGetAllMpa() {
        Collection<MpaRatings> mpaList = mpaStorage.getAllMpaRatings();
        assertThat(mpaList).isNotEmpty();
    }

    @Test
    public void testGetMpaById() {
        MpaRatings mpa = mpaStorage.getMpaRatingByIndex(1);
        assertThat(mpa).isNotNull();
        assertThat(mpa.getId()).isEqualTo(1);
    }
}