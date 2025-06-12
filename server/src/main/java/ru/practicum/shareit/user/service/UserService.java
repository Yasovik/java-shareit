package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();

    UserDto create(NewUserDto userDto);

    UserDto findById(Long userId);

    UserDto update(Long userId, UpdateUserDto userDto);

    void deleteById(Long userId);

    User validateUserExist(Long userId);
}
