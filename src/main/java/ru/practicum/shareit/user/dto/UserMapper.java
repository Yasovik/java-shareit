package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                Math.toIntExact(user.getId()),
                user.getName(),
                user.getEmail()
        );

    }

    public static User mapToNewUser(NewUserDto requestUserDto) {
        return User.builder()
                .name(requestUserDto.getName())
                .email(requestUserDto.getEmail())
                .build();
    }

    public static UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(Math.toIntExact(user.getId()))
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User updateUserFields(User user, UpdateUserDto requestUserDto) {
        if (requestUserDto.hasEmail()) {
            user.setEmail(requestUserDto.getEmail());
        }
        if (requestUserDto.hasName()) {
            user.setName(requestUserDto.getName());
        }
        return user;
    }
}