package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{idOfUser}")
    public User getUserById(@PathVariable long idOfUser) {
        return userService.getUserById(idOfUser);
    }

    @PostMapping
    public User addNewUser(@Valid @RequestBody User newUser) {
        return userService.addNewUser(newUser);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        return userService.updateUser(newUser);
    }

    @GetMapping("/{id}/friends")
    public List<User> allFriends(@PathVariable long id) {
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> allCommonFriends(@PathVariable long id,
                                       @PathVariable long otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public List<User> makeFriends(@PathVariable long id,
                                  @PathVariable long friendId) {
        return userService.makeFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public List<User> deleteFriends(@PathVariable long id,
                                    @PathVariable long friendId) {
        return userService.deleteFriends(id, friendId);
    }
}