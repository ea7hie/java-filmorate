package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
Структура теста
I - POST
    I.1 - correct fields
    I.2 - boundary conditions
        I.2.A - correct description: empty
        I.2.B - correct description: spaces
        I.2.C - correct description: 200 chars
        I.2.D - correct description: 199 chars
        I.2.E - correct date: 18-12-1895
    I.3 - incorrect name
        I.3.A - empty
        I.3.B - spaces
        I.3.C - null
    I.4 - incorrect description
        I.4.A - 201 chars
        I.4.B - null
    I.5 - incorrect releaseDate
        I.5.A - before 18-12-1895
        I.5.B - null
    I.6 - incorrect duration
        I.6.A - 0 min
        I.6.B - negative
II - GET
    II.1 - get all films
    II.2 - get film by id
III - PUT
    III.1 - correct fields
    III.2 - boundary conditions
        III.2.A - correct description: empty
        III.2.B - correct description: spaces
        III.2.C - correct description: 200 chars
        III.2.D - correct description: 199 chars
        III.2.E - correct date: 18-12-1895
    III.3 - incorrect id
        III.3.A - not exist
        III.3.B - without id
        III.3.C - null
    III.4 - incorrect name
        III.4.A - empty
        III.4.B - spaces
        III.4.C - null
    III.5 - incorrect description
        III.5.A - 201 chars
        III.5.B - null
    III.6 - incorrect releaseDate
        III.6.A - before 18-12-1895
        III.6.B - null
    III.7 - incorrect duration
        III.7.A - 0 min
        III.7.B - negative
    III.8 - add like
IV - DELETE
    IV.1 - delete like
*/

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FilmController filmController;

    @Test
    void contextLoads() {
        assertThat(filmController).isNotNull();
    }

    //I. Проверка метода POST
    //I.1 ввод всех корректных значений
    @Test
    void shouldAddNewFilm() throws Exception {
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film film = new Film("film", "desc", LocalDate.of(2000, 12, 28), 120);
        mockMvc.perform(
                post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film))
        ).andExpect(status().isOk());

        assertEquals(sizeBeforeTest + 1, filmController.getAllFilms().size());
    }

    //I.2.A пограничные данные; ввод корректного описания: пустая строка
    @Test
    void shouldAddNewFilmWithEmptyDescription() throws Exception {
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film filmWithEmptyDescription = new Film(
                "someName", "", LocalDate.of(2000, 12, 28), 120);

        mockMvc.perform(
                post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filmWithEmptyDescription))
        ).andExpect(status().isOk());

        assertEquals(sizeBeforeTest + 1, filmController.getAllFilms().size());
    }

    //I.2.B пограничные данные; ввод корректного описания: пробелы
    @Test
    void shouldAddNewFilmWithDescriptionIsOnlySpaces() throws Exception {
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film filmWithDescriptionIsOnlySpaces = new Film(
                "someName", "      ", LocalDate.of(2000, 12, 28), 120);

        mockMvc.perform(
                post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filmWithDescriptionIsOnlySpaces))
        ).andExpect(status().isOk());

        assertEquals(sizeBeforeTest + 1, filmController.getAllFilms().size());
    }

    //I.2.C пограничные данные; ввод корректного описания: 200 символов
    @Test
    void shouldAddNewFilmWithDescriptionLengthIs200() throws Exception {
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film filmWithLongDescription = new Film(
                "someName", "A".repeat(200), LocalDate.of(2000, 12, 28), 10);

        mockMvc.perform(
                post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filmWithLongDescription))
        ).andExpect(status().isOk());

        assertEquals(sizeBeforeTest + 1, filmController.getAllFilms().size());
    }

    //I.2.D пограничные данные; ввод корректного описания: 199 символов
    @Test
    void shouldAddNewFilmWithDescriptionLengthIs199() throws Exception {
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film filmWithLongDescription = new Film(
                "someName", "A".repeat(199), LocalDate.of(2000, 12, 28), 10);

        mockMvc.perform(
                post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filmWithLongDescription))
        ).andExpect(status().isOk());

        assertEquals(sizeBeforeTest + 1, filmController.getAllFilms().size());
    }

    //I.2.E пограничные данные; ввод корректной даты: 18 декабря 1895
    @Test
    void shouldAddNewFilmWithDateIsMinimum() throws Exception {
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film filmWithMinimumDate = new Film(
                "film", "desc", LocalDate.of(1895, 12, 28), 120);
        mockMvc.perform(
                post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filmWithMinimumDate))
        ).andExpect(status().isOk());

        assertEquals(sizeBeforeTest + 1, filmController.getAllFilms().size());
    }

    //I.3.A ввод некорректного имени: пустая строка
    @Test
    void shouldNotAddNewFilmWithEmptyName() throws Exception {
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film filmWithEmptyName = new Film(
                "", "desc", LocalDate.of(2000, 12, 28), 120);

        mockMvc.perform(
                post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filmWithEmptyName))
        ).andExpect(status().isBadRequest());

        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }

    //I.3.B ввод некорректного имени: строка из пробелов
    @Test
    void shouldNotAddNewFilmWithNameOfSpaces() throws Exception {
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film filmWithNameIsOnlySpaces = new Film(
                "   ", "desc", LocalDate.of(2000, 12, 28), 120);

        mockMvc.perform(
                post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filmWithNameIsOnlySpaces))
        ).andExpect(status().isBadRequest());

        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }

    //I.3.C ввод некорректного имени: null
    @Test
    void shouldNotUpdateIfNewFilmWithNameIsNull() throws Exception{
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film filmWithNameIsNull = new Film(
                null, "desc", LocalDate.of(2000, 12, 28), 120);

        mockMvc.perform(
                put("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filmWithNameIsNull))
        ).andExpect(status().isInternalServerError());

        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }

    //I.4.A ввод некорректного описания: 201 символ
    @Test
    void shouldNotAddNewFilmWithDescriptionLengthIs201() throws Exception {
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film filmWithLongDescription = new Film(
                "someName", "A".repeat(201), LocalDate.of(2000, 12, 28), 120);

        mockMvc.perform(
                post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filmWithLongDescription))
        ).andExpect(status().isBadRequest());

        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }

    //I.4.B ввод некорректного описания: null
    @Test
    void shouldNotUpdateIfNewFilmWithDescriptionIsNull() throws Exception{
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film filmWithDescriptionIsNull = new Film(
                "someName", null, LocalDate.of(2000, 12, 28), 120);

        mockMvc.perform(
                put("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filmWithDescriptionIsNull))
        ).andExpect(status().isInternalServerError());

        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }

    //I.5.A ввод некорректной даты: раньше 18 декабря 1895
    @Test
    void shouldNotAddFilmWithIncorrectDate() throws Exception {
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film filmWithIncorrectDate = new Film(
                "someName", "someDescription",
                LocalDate.of(1895, 12, 27), 120);

        mockMvc.perform(
                post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filmWithIncorrectDate))
        ).andExpect(status().isBadRequest());

        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }

    //I.5.B ввод некорректной даты: null
    @Test
    void shouldNotUpdateIfNewFilmWithReleaseDateIsNull() throws Exception{
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film filmWithReleaseDateIsNull = new Film(
                "someName", "someDescription", null, 120);

        mockMvc.perform(
                put("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filmWithReleaseDateIsNull))
        ).andExpect(status().isInternalServerError());

        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }

    //I.6.A ввод некорректной длительности: 0 минут
    @Test
    void shouldNotAddFilmWithIncorrectDurationIsZero() throws Exception {
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film filmWithIncorrectDuration = new Film(
                "someName", "someDescription",
                LocalDate.of(2012, 12, 27), 0);

        mockMvc.perform(
                post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filmWithIncorrectDuration))
        ).andExpect(status().isBadRequest());

        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }

    //I.6.B ввод некорректной длительность: отрицательное число
    @Test
    void shouldNotAddFilmWithIncorrectDurationIsNegative() throws Exception {
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film filmWithIncorrectDuration = new Film(
                "someName", "someDescription",
                LocalDate.of(2012, 12, 27), -100);

        mockMvc.perform(
                post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filmWithIncorrectDuration))
        ).andExpect(status().isBadRequest());

        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }


    //II. Проверка метода GET
    //II.1 вывод всех фильмов
    @Test
    void shouldGetListOfAddedFilm() throws Exception {
        int sizeBeforeTest = filmController.getAllFilms().size();

        mockMvc.perform(
                get("/films")
                        .contentType("application/json")
        ).andExpect(status().isOk());

        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }

    //II.2 вывод фильма по id
    @Test
    void shouldGetilm() throws Exception {
        int sizeBeforeTest = filmController.getAllFilms().size();

        mockMvc.perform(
                        get("/films/1")
                                .contentType("application/json")
                ).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(filmController.getFilmById(1))));

        mockMvc.perform(
                        get("/films/100000")
                                .contentType("application/json")
                ).andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));

        mockMvc.perform(
                        get("/films/-10")
                                .contentType("application/json")
                ).andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));

        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }


    //III. Проверка метода PUT
    //III.1 ввод всех корректных значений
    @Test
    void shouldUpdateFilm() throws Exception {
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film newFilmForUpdate = new Film(
                1L,
                "Пираты Карибского моря. Проклятие чёрной жемчужины",
                "Капитан Джек Воробей пытается вернуть свой корабль, который захватил другой пират, " +
                        "который сам в данный момент пытается избавится от проклятия",
                LocalDate.of(2003, 7, 9),
                143
        );

        mockMvc.perform(
                put("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newFilmForUpdate))
        ).andExpect(status().isOk());

        boolean isFilmUpdated = filmController.getAllFilms().stream()
                .map(Film::getName)
                .anyMatch(name -> name.equals("Пираты Карибского моря. Проклятие чёрной жемчужины"));

        Film film1 = filmController.getAllFilms().stream()
                .filter(film -> film.getId() == 1)
                .findFirst()
                .get();

        assertTrue(isFilmUpdated);
        assertEquals(newFilmForUpdate, film1);
        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }

    //III.2.A пограничные данные; ввод корректного описания: пустая строка
    @Test
    void shouldUpdateFilmWithEmptyDescription() throws Exception {
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film filmWithEmptyDescription = new Film(
                1L, "someName", "", LocalDate.of(2000, 12, 28), 120);

        mockMvc.perform(
                put("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filmWithEmptyDescription))
        ).andExpect(status().isOk());

        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }

    //III.2.B пограничные данные; ввод корректного описания: пробелы
    @Test
    void shouldUpdateFilmWithDescriptionIsOnlySpaces() throws Exception {
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film filmWithDescriptionIsOnlySpaces = new Film(
                1L, "someName", "  ", LocalDate.of(2000, 12, 28), 9);

        mockMvc.perform(
                put("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filmWithDescriptionIsOnlySpaces))
        ).andExpect(status().isOk());

        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }

    //III.2.C пограничные данные; ввод корректного описания: 200 символов
    @Test
    void shouldUpdateFilmWithDescriptionLengthIs200() throws Exception {
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film filmWithLongDescription = new Film(1L, "someName", "A".repeat(200),
                LocalDate.of(2000, 12, 28), 10);

        mockMvc.perform(
                put("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filmWithLongDescription))
        ).andExpect(status().isOk());

        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }

    //III.2.D пограничные данные; ввод корректного описания: 199 символов
    @Test
    void shouldUpdateFilmWithDescriptionLengthIs199() throws Exception {
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film filmWithLongDescription = new Film(1L, "someName", "A".repeat(199),
                LocalDate.of(2000, 12, 28), 10);

        mockMvc.perform(
                put("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filmWithLongDescription))
        ).andExpect(status().isOk());

        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }

    //III.2.E пограничные данные; ввод корректной даты: 18 декабря 1895
    @Test
    void shouldUpdateFilmWithDateIsMinimum() throws Exception {
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film filmWithMinimumDate = new Film(1L, "film", "desc",
                LocalDate.of(1895, 12, 28), 120);

        mockMvc.perform(
                put("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filmWithMinimumDate))
        ).andExpect(status().isOk());

        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }

    //III.3.A ввод некорректного id: несуществующий id
    @Test
    void shouldNotUpdateFilmWithNotExistingId() {
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film newFilmForUpdate = new Film(
                10000L,
                "Пираты Карибского моря. Проклятие чёрной жемчужины",
                "Капитан Джек Воробей пытается вернуть свой корабль, который захватил другой пират, " +
                        "который сам в данный момент пытается избавится от проклятия",
                LocalDate.of(2003, 7, 9),
                143
        );

        assertThrows(NotFoundException.class, () -> filmController.updateFilm(newFilmForUpdate));

        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }

    //III.3.B ввод некорректного id: id не указан
    @Test
    void shouldNotUpdateFilmWithoutId() {
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film newFilmForUpdate = new Film(
                "Пираты Карибского моря. Проклятие чёрной жемчужины",
                "Капитан Джек Воробей пытается вернуть свой корабль, который захватил другой пират, " +
                        "который сам в данный момент пытается избавится от проклятия",
                LocalDate.of(2003, 7, 9),
                143
        );

        assertThrows(NotFoundException.class, () -> filmController.updateFilm(newFilmForUpdate));

        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }

    //III.3.C ввод некорректного id: null
    @Test
    void shouldNotUpdateFilmWithIdIsNull() {
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film newFilmForUpdate = new Film(
                null,
                "Пираты Карибского моря. Проклятие чёрной жемчужины",
                "Капитан Джек Воробей пытается вернуть свой корабль, который захватил другой пират, " +
                        "который сам в данный момент пытается избавится от проклятия",
                LocalDate.of(2003, 7, 9),
                143
        );

        assertThrows(NotFoundException.class, () -> filmController.updateFilm(newFilmForUpdate));

        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }

    //III.4.A ввод некорректного имени: пустая строка
    @Test
    void shouldNotUpdateFilmWithEmptyName() throws Exception {
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film newFilmForUpdate = new Film(
                1L,
                "",
                "Капитан Джек Воробей пытается вернуть свой корабль, который захватил другой пират, " +
                        "который сам в данный момент пытается избавится от проклятия",
                LocalDate.of(2003, 7, 9),
                143
        );

        mockMvc.perform(
                put("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newFilmForUpdate))
        ).andExpect(status().isBadRequest());

        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }

    //III.4.B ввод некорректного имени: строка из пробелов
    @Test
    void shouldNotAUpdateFilmWithNameOfSpaces() throws Exception {
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film filmWithNameIsOnlySpaces = new Film(1L, "   ", "desc",
                LocalDate.of(2000, 12, 28), 120);

        mockMvc.perform(
                put("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filmWithNameIsOnlySpaces))
        ).andExpect(status().isBadRequest());

        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }

    //III.4.C ввод некорректного имени: null
    @Test
    void shouldNotUpdateIfUpdateFilmWithNameIsNull() throws Exception {
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film filmWithNameIsNull = new Film(1L, null, "desc",
                LocalDate.of(2000, 12, 28), 120);

        mockMvc.perform(
                put("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filmWithNameIsNull))
        ).andExpect(status().isInternalServerError());

        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }

    //III.5.A ввод некорректного описания: 201 символ
    @Test
    void shouldNotUpdateFilmWithDescriptionLengthIs201() throws Exception {
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film filmWithLongDescription = new Film(1L, "someName", "A".repeat(201),
                LocalDate.of(2000, 12, 28), 120);

        mockMvc.perform(
                put("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filmWithLongDescription))
        ).andExpect(status().isBadRequest());

        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }

    //III.5.B ввод некорректного описания: null
    @Test
    void shouldNotUpdateIfUpdateFilmWithDescriptionIsNull() throws Exception{
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film filmWithDescriptionIsNull = new Film(1L, "someName", null,
                LocalDate.of(2000, 12, 28), 120);

        mockMvc.perform(
                put("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filmWithDescriptionIsNull))
        ).andExpect(status().isInternalServerError());

        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }

    //III.6.A ввод некорректной даты: раньше 18 декабря 1895
    @Test
    void shouldNotUpdateFilmWithIncorrectDate() throws Exception {
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film filmWithIncorrectDate = new Film(1L, "someName", "someDescription",
                LocalDate.of(1895, 12, 27), 120);

        mockMvc.perform(
                put("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filmWithIncorrectDate))
        ).andExpect(status().isBadRequest());

        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }

    //III.6.B ввод некорректной даты: null
    @Test
    void shouldNotUpdateIfUpdateFilmWithReleaseDateIsNull() throws Exception{
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film filmWithReleaseDateIsNull = new Film(1L, "someName", "someDescription",
                null, 120);

        mockMvc.perform(
                put("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filmWithReleaseDateIsNull))
        ).andExpect(status().isInternalServerError());

        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }

    //III.7.A ввод некорректной длительности: 0 минут
    @Test
    void shouldNotUpdateFilmWithIncorrectDurationIsZero() throws Exception {
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film filmWithIncorrectDuration = new Film(1L, "someName", "someDescription",
                LocalDate.of(2012, 12, 27), 0);

        mockMvc.perform(
                put("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filmWithIncorrectDuration))
        ).andExpect(status().isBadRequest());

        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }

    //III.7.B ввод некорректной длительность: отрицательное число
    @Test
    void shouldNotUpdateFilmWithIncorrectDurationIsNegative() throws Exception {
        int sizeBeforeTest = filmController.getAllFilms().size();

        Film filmWithIncorrectDuration = new Film(1L, "someName", "someDescription",
                LocalDate.of(2012, 12, 27), -100);

        mockMvc.perform(
                post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filmWithIncorrectDuration))
        ).andExpect(status().isBadRequest());

        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }

    //III.8 добавление лайка
    @Test
    void shouldAddLike() throws Exception {
        int sizeBeforeTest = filmController.getAllFilms().size();

        User newUser = new User("Диана", "ea7hie@mail.ru", "ea7hie",
                LocalDate.of(2005, 1, 7));

        mockMvc.perform(
                post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newUser))
        ).andExpect(status().isOk());

        mockMvc.perform(
                put("/films/1/like/1")
                        .contentType("application/json")
        ).andExpect(status().isOk());

        mockMvc.perform(
                        put("/films/11/like/1")
                                .contentType("application/json")
                ).andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));


        mockMvc.perform(
                        put("/films/1/like/11")
                                .contentType("application/json")
                ).andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));


        mockMvc.perform(
                        put("/films/11/like/11")
                                .contentType("application/json")
                ).andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));

        assertTrue(filmController.getFilmById(1L).getIdsOfAllUsersWhoLike().size() == 1);
        assertTrue(filmController.getFilmById(1L).getIdsOfAllUsersWhoLike().contains(1L));
        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }


    //IV - Проверка метода DELETE
    //IV.1 удаление лайка
    @Test
    void shouldDeleteLike() throws Exception {
        int sizeBeforeTest = filmController.getAllFilms().size();

        mockMvc.perform(
                delete("/films/1/like/1")
                        .contentType("application/json")
        ).andExpect(status().isOk());

        mockMvc.perform(
                        delete("/films/11/like/1")
                                .contentType("application/json")
                ).andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));


        mockMvc.perform(
                        delete("/films/1/like/11")
                                .contentType("application/json")
                ).andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));


        mockMvc.perform(
                        delete("/films/11/like/11")
                                .contentType("application/json")
                ).andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));

        assertTrue(filmController.getFilmById(1L).getIdsOfAllUsersWhoLike().isEmpty());
        assertEquals(sizeBeforeTest, filmController.getAllFilms().size());
    }
}