package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.user.UserMapper.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(UserMapper::mapToUserDto).toList();
    }

    @Transactional
    @Override
    public UserDto create(NewUserDto userDto) {
        validateEmailExist(userDto.getEmail());
        return mapToUserDto(userRepository.save(mapToNewUser(userDto)));
    }

    @Override
    public UserDto findById(Long userId) {
        return mapToUserDto(validateUserExist(userId));
    }

    @Transactional
    @Override
    public UserDto update(Long userId, UpdateUserDto userDto) {
        User user = validateUserExist(userId);
        validateEmailExist(userDto.getEmail(), user.getId());
        updateUserFields(user, userDto);
        userRepository.save(user);
        return mapToUserDto(user);
    }

    @Transactional
    @Override
    public void deleteById(Long userId) {
        validateUserExist(userId);
        userRepository.deleteById(userId);
    }

    @Override
    public User validateUserExist(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id %d не найден.", userId)));
    }

    private void validateEmailExist(String email, Long currentUserId) {
        Optional<User> alreadyExistUser = userRepository.findByEmail(email);
        if (alreadyExistUser.isPresent() && !alreadyExistUser.get().getId().equals(currentUserId)) {
            throw new DuplicatedDataException(String.format("Email - %s уже используется", email));
        }
    }

    private void validateEmailExist(String email) {
        Optional<User> alreadyExistUser = userRepository.findByEmail(email);
        if (alreadyExistUser.isPresent()) {
            throw new DuplicatedDataException(String.format("Email - %s уже используется", email));
        }
    }
}
