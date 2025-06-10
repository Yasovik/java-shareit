package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceTest {
    @Autowired
    UserService userService;

    static NewUserDto user1;
    static NewUserDto user2;

    @BeforeAll
    static void beforeAll() {
        user1 = NewUserDto.builder().name("Yandex").email("yandex@practicum.ru").build();
        user2 = NewUserDto.builder().name("Yandex2").email("yandex2@practicum.ru").build();
    }

    @Test
    void getAllUsers() {
        userService.create(user1);
        UserDto newUser = userService.create(user2);
        List<UserDto> users = userService.findAll().stream().toList();

        assertThat(users.get(1).getId()).isEqualTo(newUser.getId());
        assertThat(users.get(1).getName()).isEqualTo(newUser.getName());
        assertThat(users.get(1).getEmail()).isEqualTo(newUser.getEmail());
    }

    @Test
    void createAndGetUser() {
        UserDto user = userService.create(user1);
        UserDto getUser = userService.findById(user.getId());

        assertThat(user.getId()).isEqualTo(getUser.getId());
        assertThat(user.getName()).isEqualTo(getUser.getName());
        assertThat(user.getEmail()).isEqualTo(getUser.getEmail());
    }

    @Test
    void throwExceptionWhenEmailIsDuplicateWhenCreateUser() {
        userService.create(user1);
        NewUserDto newUser = NewUserDto.builder().name("Yandex2").email("yandex@practicum.ru").build();

        assertThatThrownBy(() -> userService.create(newUser)).isInstanceOf(DuplicatedDataException.class);
    }

    @Test
    void throwExceptionWhenIdIsNull() {
        assertThatThrownBy(() -> userService.findById(null)).isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    void updateUser() {
        UserDto user = userService.create(user1);
        UpdateUserDto updateUserDto = UpdateUserDto.builder().name(user2.getName()).email(user2.getEmail()).build();
        UserDto updateUser = userService.update(user.getId(), updateUserDto);

        assertThat(updateUser.getId()).isEqualTo(user.getId());
        assertThat(updateUser.getName()).isEqualTo(user2.getName());
        assertThat(updateUser.getEmail()).isEqualTo(user2.getEmail());
    }

    @Test
    void updateUserNameIsNull() {
        UserDto user = userService.create(user1);
        UpdateUserDto userUpdateDto = UpdateUserDto.builder().email("yandex2@practicum.ru").build();
        UserDto updateUser = userService.update(user.getId(), userUpdateDto);

        assertThat(updateUser.getId()).isEqualTo(user.getId());
        assertThat(updateUser.getName()).isEqualTo(user.getName());
        assertThat(updateUser.getEmail()).isEqualTo(userUpdateDto.getEmail());
    }

    @Test
    void updateUserEmailIsNull() {
        UserDto user = userService.create(user1);
        UpdateUserDto userUpdateDto = UpdateUserDto.builder().name("Yandex2").build();
        UserDto updateUser = userService.update(user.getId(), userUpdateDto);

        assertThat(updateUser.getId()).isEqualTo(user.getId());
        assertThat(updateUser.getName()).isEqualTo(userUpdateDto.getName());
        assertThat(updateUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void throwExceptionWhenEmailIsDuplicateWhenUpdateUser() {
        UserDto user = userService.create(user1);
        UserDto user3 = userService.create(user2);
        UpdateUserDto updateUser = UpdateUserDto.builder().name("Yandex2").email("yandex@practicum.ru").build();

        assertThatThrownBy(() -> userService.update(user3.getId(), updateUser))
                .isInstanceOf(DuplicatedDataException.class);
    }

    @Test
    void deleteUser() {
        UserDto user = userService.create(user1);
        userService.deleteById(user.getId());

        assertThatThrownBy(() -> userService.findById(user.getId()))
                .isInstanceOf(NotFoundException.class);
    }
}
