package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(User user);

    UserDto update(UserDto user, Integer id);

    UserDto getUser(Integer id);

    List<UserDto> getAllUsers();

    void delete(Integer id);
}