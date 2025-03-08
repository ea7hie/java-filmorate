package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
III - PUT
    III.1 - correct fields
        III.1.A - with name
        III.1.B - without name
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

        User newUser = new User("", "ea7hie@gmail.com", "login",
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

        User newUser = new User("     ", "ea7hie@gmail.com", "login",
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

        User newUser = new User(isNull, "ea7hie@gmail.com", "login",
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

        User newUser = new User("ea7hie@gmail.com", "login",
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

        User newUser = new User("ea7hie@gmail.com", "login",
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
       /* assertThrows(NullPointerException.class, () -> {

        });*/

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
    @Test
    void shouldGet() throws Exception {
        int sizeBeforeTest = userController.getAllUsers().size();

        mockMvc.perform(
                get("/users")
                        .contentType("application/json")
        ).andExpect(status().isOk());

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

        User newUser = new User(1L, "ea7hie56@gmail.com", "login",
                LocalDate.of(2005, Month.JANUARY, 7));

        mockMvc.perform(
                put("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newUser))
        ).andExpect(status().isOk());

        assertEquals(sizeBeforeTest, userController.getAllUsers().size());
    }
}