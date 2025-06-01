package ru.practicum.shareit.user;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Transactional(readOnly = true)
public interface UserService {
    @Transactional
    UserDto create(NewUserDto user);

    @Transactional
    UserDto update(Long userId, UpdateUserDto userDto);

    UserDto getUser(Integer id);

    List<UserDto> getAllUsers();

    @Transactional
    void delete(Integer id);

    User validateUserExist(Long userId);
}