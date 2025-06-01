package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @PostMapping
    public UserDto create(@Valid @RequestBody NewUserDto user) {
        log.info("Запрос на создание пользователя");
        return service.create(user);
    }

    @PatchMapping("/{id}")
    public UserDto update(@Valid @RequestBody UpdateUserDto userDto, @PathVariable("id") Long id) {
        log.info("Запрос на обновление пользователя");
        return service.update(id, userDto);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable("id") Integer id) {
        log.info("Запрос на получение пользователя с id:{}", id);
        return service.getUser(id);
    }

    @GetMapping()
    public List<UserDto> getUser() {
        log.info("Запрос на получение всех пользователей");
        return service.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        log.info("Запрос на удаление пользователя");
        service.delete(id);
    }
}