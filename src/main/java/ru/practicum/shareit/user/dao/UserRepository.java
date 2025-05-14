package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.*;

@Slf4j
@Repository
public class UserRepository {

    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    public UserDto create(User user) {
        user.setId(id);
        users.put(id, user);
        id++;
        log.info("Успешно создан пользователь {}", user);
        return UserMapper.toUserDto(user);
    }

    public UserDto update(UserDto userDto, int userId) {
        User newUser = users.get(userId);
        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty() && !userDto.getEmail().isBlank()) {
            newUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null && !userDto.getName().isEmpty() && !userDto.getName().isBlank()) {
            newUser.setName(userDto.getName());
        }
        users.put(id, newUser);
        log.info("Успешно обновлен пользователь {}", newUser);
        return UserMapper.toUserDto(newUser);
    }

    public UserDto getUser(int id) {
        try {
            return UserMapper.toUserDto(users.get(id));
        } catch (NullPointerException e) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
    }

    public List<UserDto> getAllUsers() {
        return Optional.of(users)
                .map(map -> map.values().stream()
                        .map(UserMapper::toUserDto)
                        .toList())
                .orElse(Collections.emptyList());
    }

    public void delete(int id) {
        users.remove(id);
        log.info("Успешно удален пользователь c id {}", id);
    }

    public void checkUserExist(String email) {
        Optional<User> existingUser = users.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();

        if (existingUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Пользователь с email " + email + " уже существует");
        }
    }

}