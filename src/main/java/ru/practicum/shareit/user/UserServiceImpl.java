package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.user.dto.UserMapper.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserRepository storage;

    public UserServiceImpl(UserRepository storage) {
        this.storage = storage;
    }


    @Override
    public UserDto create(NewUserDto userDto) {
        validateEmailExist(userDto.getEmail());
        return mapToUserDto(storage.save(mapToNewUser(userDto)));
    }

    @Override
    public UserDto update(Long userId, UpdateUserDto userDto) {
        User user = validateUserExist(userId);
        validateEmailExist(userDto.getEmail(), user.getId());
        updateUserFields(user, userDto);
        storage.save(user);
        return mapToUserDto(user);
    }


    @Override
    public UserDto getUser(Integer id) {
        return mapToUserDto(validateUserExist(Long.valueOf(id)));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return storage.findAll().stream().map(UserMapper::mapToUserDto).toList();
    }

    @Override
    public void delete(Integer id) {
        validateUserExist(Long.valueOf(id));
        storage.deleteById(Long.valueOf(id));
    }

    @Override
    public User validateUserExist(Long userId) {
        return storage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id %d не найден.", userId)));
    }

    private void validateEmailExist(String email) {
        Optional<User> alreadyExistUser = storage.findByEmail(email);
        if (alreadyExistUser.isPresent()) {
            throw new DuplicatedDataException(String.format("Email - %s уже используется", email));
        }
    }

    private void validateEmailExist(String email, Long currentUserId) {
        Optional<User> alreadyExistUser = storage.findByEmail(email);
        if (alreadyExistUser.isPresent() && !alreadyExistUser.get().getId().equals(currentUserId)) {
            throw new DuplicatedDataException(String.format("Email - %s уже используется", email));
        }
    }

}