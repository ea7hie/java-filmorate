package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

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
        I.2.A - correct name: empty
        I.2.B - correct name: spaces
        I.2.C - correct name: null
        I.2.D - correct name: without name
        I.2.E - correct date: yesterday
    I.3 - incorrect login
        I.3.A - empty
        I.3.B - spaces
        I.3.C - null
    I.4 - incorrect email
        I.4.A - empty
        I.4.B - spaces
        I.4.C - null
        I.4.D - wrong
    I.5 - incorrect birthday
        I.5.A - today
        I.5.B - tomorrow
        I.5.C - null
II - GET
    II.1 - get all users
    II.2 - get user by id
    II.3 - get all friends user by id
    II.4 - get common friends
III - PUT
    III.1 - correct fields
        III.1.A - with name
        III.1.B - without name
    III.2 - make friends
IV - DELETE
    IV.1 - delete friend
*/

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserController userController;

    @Autowired
    private FilmController filmController;

    @Test
    void contextLoads() {
        assertThat(userController).isNotNull();
    }

    //I. Проверка метода POST
    //I.1 ввод всех корректных значений
    @Test
    void shouldAddNewUser() throws Exception {
        int sizeBeforeTest = userController.getAllUsers().size();

        User newUser = new User("name", "ea7hie@gmail.com", "login",
                LocalDate.of(2005, Month.JANUARY, 7));

        mockMvc.perform(
                post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newUser))
        ).andExpect(status().isOk());

        assertEquals(sizeBeforeTest + 1, userController.getAllUsers().size());
    }

    //I.2.A пограничные данные; ввод корректного имени: пустая строка
    @Test
    void shouldAddNewUserWithEmptyName() throws Exception {
        int sizeBeforeTest = userController.getAllUsers().size();

        User newUser = new User("", "ea7hie11@gmail.com", "login",
                LocalDate.of(2005, Month.JANUARY, 7));

        mockMvc.perform(
                post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newUser))
        ).andExpect(status().isOk());

        assertEquals(sizeBeforeTest + 1, userController.getAllUsers().size());
    }

    //I.2.B пограничные данные; ввод корректного имени: пробелы
    @Test
    void shouldAddNewUserWithNameIsSpaces() throws Exception {
        int sizeBeforeTest = userController.getAllUsers().size();

        User newUser = new User("     ", "ea7hie98@gmail.com", "login",
                LocalDate.of(2005, Month.JANUARY, 7));

        mockMvc.perform(
                post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newUser))
        ).andExpect(status().isOk());

        assertEquals(sizeBeforeTest + 1, userController.getAllUsers().size());
    }

    //I.2.C пограничные данные; ввод корректного имени: null
    @Test
    void shouldAddNewUserWithNameIsNull() throws Exception {
        int sizeBeforeTest = userController.getAllUsers().size();
        String isNull = null;

        User newUser = new User(isNull, "ea7hie14@gmail.com", "login",
                LocalDate.of(2005, Month.JANUARY, 7));

        mockMvc.perform(
                post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newUser))
        ).andExpect(status().isOk());

        assertEquals(sizeBeforeTest + 1, userController.getAllUsers().size());
    }

    //I.2.D пограничные данные; ввод корректного имени: без имени
    @Test
    void shouldAddNewUserWithoutName() throws Exception {
        int sizeBeforeTest = userController.getAllUsers().size();

        User newUser = new User("ea7hi123e@gmail.com", "login",
                LocalDate.of(2005, Month.JANUARY, 7));

        mockMvc.perform(
                post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newUser))
        ).andExpect(status().isOk());

        assertEquals(sizeBeforeTest + 1, userController.getAllUsers().size());
    }

    //I.2.E пограничные данные; ввод корректной даты: вчера
    @Test
    void shouldAddNewUserWithBirthdayIsYesterday() throws Exception {
        int sizeBeforeTest = userController.getAllUsers().size();

        User newUser = new User("ea7hie56@gmail.com", "login",
                LocalDate.now().minusDays(1));

        mockMvc.perform(
                post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newUser))
        ).andExpect(status().isOk());

        assertEquals(sizeBeforeTest + 1, userController.getAllUsers().size());
    }

    //I.3.A ввод некорректного логина: пустая строка
    @Test
    void shouldNotAddNewUserWithLoginIsEmpty() throws Exception {
        int sizeBeforeTest = userController.getAllUsers().size();

        User newUser = new User("name", "ea7hie@gmail.com", "",
                LocalDate.of(2005, Month.JANUARY, 7));

        mockMvc.perform(
                post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newUser))
        ).andExpect(status().isBadRequest());

        assertEquals(sizeBeforeTest, userController.getAllUsers().size());
    }

    //I.3.B ввод некорректного логина: пробелы
    @Test
    void shouldNotAddNewUserWithLoginIsSpaces() throws Exception {
        int sizeBeforeTest = userController.getAllUsers().size();

        User newUser = new User("name", "ea7hie@gmail.com", "        ",
                LocalDate.of(2005, Month.JANUARY, 7));

        mockMvc.perform(
                post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newUser))
        ).andExpect(status().isBadRequest());

        assertEquals(sizeBeforeTest, userController.getAllUsers().size());
    }

    //I.3.C ввод некорректного логина: null
    @Test
    void shouldNotAddNewUserWithLoginIsNull() throws Exception {
        int sizeBeforeTest = userController.getAllUsers().size();

        User newUser = new User("name", "ea7hie@gmail.com", null,
                LocalDate.of(2005, Month.JANUARY, 7));

        mockMvc.perform(
                post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newUser))
        ).andExpect(status().isBadRequest());

        assertEquals(sizeBeforeTest, userController.getAllUsers().size());
    }

    //I.4.A ввод некорректного имайла: пустая строка
    @Test
    void shouldNotAddNewUserWithEmailIsEmpty() throws Exception {
        int sizeBeforeTest = userController.getAllUsers().size();

        User newUser = new User("name", "", "login",
                LocalDate.of(2005, Month.JANUARY, 7));

        mockMvc.perform(
                post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newUser))
        ).andExpect(status().isBadRequest());

        assertEquals(sizeBeforeTest, userController.getAllUsers().size());
    }

    //I.4.B ввод некорректного имайла: пробелы
    @Test
    void shouldNotAddNewUserWithEmailIsSpaces() throws Exception {
        int sizeBeforeTest = userController.getAllUsers().size();

        User newUser = new User("name", "      ", "login",
                LocalDate.of(2005, Month.JANUARY, 7));

        mockMvc.perform(
                post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newUser))
        ).andExpect(status().isBadRequest());

        assertEquals(sizeBeforeTest, userController.getAllUsers().size());
    }

    //I.4.C ввод некорректного имайла: null
    @Test
    void shouldNotAddNewUserWithEmailIsNull() throws Exception {
        int sizeBeforeTest = userController.getAllUsers().size();
        User newUser = new User("name", null, "login",
                LocalDate.of(2005, Month.JANUARY, 7));

        mockMvc.perform(
                post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newUser))
        ).andExpect(status().isBadRequest());

        assertEquals(sizeBeforeTest, userController.getAllUsers().size());
    }

    //I.4.D ввод некорректного имайла: проверка на валидность
    @Test
    void shouldNotAddNewUserWithEmailIsWrang() throws Exception {
        int sizeBeforeTest = userController.getAllUsers().size();

        User newUser = new User("name", "ЧТО_?-уГоДнО@", "login",
                LocalDate.of(2005, Month.JANUARY, 7));

        mockMvc.perform(
                post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newUser))
        ).andExpect(status().isBadRequest());

        assertEquals(sizeBeforeTest, userController.getAllUsers().size());
    }

    //I.5.A ввод некорректной даты рождения: сегодня
    @Test
    void shouldNotAddNewUserWithBirthdayIsToday() throws Exception {
        int sizeBeforeTest = userController.getAllUsers().size();

        User newUser = new User("name", "ea7hie@mail.ru", "login",
                LocalDate.now());

        mockMvc.perform(
                post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newUser))
        ).andExpect(status().isBadRequest());

        assertEquals(sizeBeforeTest, userController.getAllUsers().size());
    }


    //I.5.B ввод некорректной даты рождения: завтра
    @Test
    void shouldNotAddNewUserWithBirthdayIsTomorrow() throws Exception {
        int sizeBeforeTest = userController.getAllUsers().size();

        User newUser = new User("name", "ea7hie@mail.ru", "login",
                LocalDate.now().plusDays(1));

        mockMvc.perform(
                post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newUser))
        ).andExpect(status().isBadRequest());

        assertEquals(sizeBeforeTest, userController.getAllUsers().size());
    }

    //I.5.C ввод некорректной даты рождения: null
    @Test
    void shouldNotAddNewUserWithBirthdayIsNull() throws Exception {
        int sizeBeforeTest = userController.getAllUsers().size();

        User newUser = new User("name", "ea7hie@mail.ru", "login",
                null);
        mockMvc.perform(
                post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newUser))
        ).andExpect(status().isBadRequest());

        assertEquals(sizeBeforeTest, userController.getAllUsers().size());
    }


    //II. Проверка метода GET
    //II.1 - вывод всех пользователей
    @Test
    void shouldGet() throws Exception {
        int sizeBeforeTest = userController.getAllUsers().size();

        mockMvc.perform(
                get("/users")
                        .contentType("application/json")
        ).andExpect(status().isOk());

        assertEquals(sizeBeforeTest, userController.getAllUsers().size());
    }

    //II.2 - вывод пользователя по id
    @Test
    void shouldGetById() throws Exception {
        int sizeBeforeTest = userController.getAllUsers().size();

        mockMvc.perform(
                        get("/users/1")
                                .contentType("application/json")
                ).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userController.getUserById(1))));

        mockMvc.perform(
                        get("/users/100000")
                                .contentType("application/json")
                ).andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));

        mockMvc.perform(
                        get("/users/-10")
                                .contentType("application/json")
                ).andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));

        assertEquals(sizeBeforeTest, userController.getAllUsers().size());
    }

    //II.3 - вывод друзей пользователя по id
    @Test
    void shouldGetAllFriends() throws Exception {
        int sizeBeforeTest = userController.getAllUsers().size();

        mockMvc.perform(
                get("/users/1/friends")
                        .contentType("application/json")
        ).andExpect(status().isOk());

        mockMvc.perform(
                        get("/users/1000000/friends")
                                .contentType("application/json")
                ).andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));

        assertEquals(sizeBeforeTest, userController.getAllUsers().size());
    }

    //II.4 - вывод общих друзей пользователей
    @Test
    void shouldGetCommonFriends() throws Exception {
        int sizeBeforeTest = userController.getAllUsers().size();

        mockMvc.perform(
                get("/users/1/friends/common/2")
                        .contentType("application/json")
        ).andExpect(status().isOk());

        mockMvc.perform(
                        get("/users/1/friends/common/200000")
                                .contentType("application/json")
                ).andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));

        assertEquals(sizeBeforeTest, userController.getAllUsers().size());
    }


    //III. Проверка метода PUT
    //III.1.A ввод всех корректных значений: есть name
    @Test
    void shouldUpdateUser() throws Exception {
        int sizeBeforeTest = userController.getAllUsers().size();

        User newUser = new User(1L, "newName", "ea7hie@gmail.com", "login",
                LocalDate.of(2005, Month.JANUARY, 7));

        mockMvc.perform(
                put("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newUser))
        ).andExpect(status().isOk());

        assertEquals(sizeBeforeTest, userController.getAllUsers().size());
    }

    //III.1.B ввод всех корректных значений: без name
    @Test
    void shouldUpdateUserWithoutName() throws Exception {
        int sizeBeforeTest = userController.getAllUsers().size();

        User newUser = new User(1L, "ea7hie561@gmail.com", "login",
                LocalDate.of(2005, Month.JANUARY, 7));

        mockMvc.perform(
                put("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newUser))
        ).andExpect(status().isOk());

        assertEquals(sizeBeforeTest, userController.getAllUsers().size());
    }

    //III.2 добавление в друзья
    @Test
    void shouldMakeFriends() throws Exception {
        int sizeBeforeTest = userController.getAllUsers().size();

        mockMvc.perform(
                put("/users/1/friends/2")
                        .contentType("application/json")
        ).andExpect(status().isOk());

        assertTrue(userController.getUserById(1).getIdsOfAllFriends().contains(2L));
        assertTrue(userController.getUserById(2).getIdsOfAllFriends().contains(1L));
        assertTrue(userController.allFriends(1).contains(userController.getUserById(2)));
        assertTrue(userController.allFriends(2).contains(userController.getUserById(1)));
        assertEquals(sizeBeforeTest, userController.getAllUsers().size());
    }


    //IV. Проверка метода DELETE
    //IV.1 удаление из друзей
    @Test
    void shouldDeleteFriends() throws Exception {
        int sizeBeforeTest = userController.getAllUsers().size();

        mockMvc.perform(
                delete("/users/1/friends/2")
                        .contentType("application/json")
        ).andExpect(status().isOk());

        assertFalse(userController.getUserById(1).getIdsOfAllFriends().contains(2L));
        assertFalse(userController.getUserById(2).getIdsOfAllFriends().contains(1L));
        assertFalse(userController.allFriends(1).contains(userController.getUserById(2)));
        assertFalse(userController.allFriends(2).contains(userController.getUserById(1)));
        assertEquals(sizeBeforeTest, userController.getAllUsers().size());
    }
}