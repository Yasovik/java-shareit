package ru.practicum.shareit.request;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.request.dto.NewRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestServiceTest {
    @Autowired
    UserService userService;

    @Autowired
    RequestService requestService;

    static NewUserDto user1;
    static NewItemDto item1;
    static NewRequestDto itemRequest1;

    @BeforeAll
    static void beforeAll() {
        user1 = NewUserDto.builder().name("Yandex").email("yandex@practicum.ru").build();
        item1 = NewItemDto.builder().name("Yandex").description("YandexPracticum").available(true).build();
        itemRequest1 = NewRequestDto.builder().description("YandexPracticum").build();
    }

    @Test
    void createAndGetRequest() {
        UserDto user = userService.create(user1);
        RequestDto requestDto = requestService.create(user.getId(), itemRequest1);
        RequestDto getItemRequest = requestService.findById(user.getId(), requestDto.getId());

        assertThat(requestDto.getId()).isEqualTo(getItemRequest.getId());
        assertThat(requestDto.getDescription()).isEqualTo(getItemRequest.getDescription());
        assertThat(requestDto.getRequesterName()).isEqualTo(getItemRequest.getRequesterName());
    }

    @Test
    void throwExceptionWhenUserIsNotFound() {
        assertThatThrownBy(() -> requestService.create(1000L, itemRequest1))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void throwExceptionWhenIdIsNull() {
        assertThatThrownBy(() -> requestService.create(null, itemRequest1))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    void getItemRequestByRequestorId() {
        UserDto user = userService.create(user1);
        RequestDto itemRequest = requestService.create(user.getId(), itemRequest1);

        List<RequestDto> itemRequests = requestService.getAllRequestsById(user.getId()).stream()
                .toList();

        assertThat(itemRequests).hasSize(1);
        assertThat(itemRequests.getFirst().getId()).isEqualTo(itemRequest.getId());
        assertThat(itemRequests.getFirst().getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(itemRequests.getFirst().getRequesterName()).isEqualTo(itemRequest.getRequesterName());
    }

    @Test
    void getAllRequests() {
        UserDto user = userService.create(user1);
        RequestDto itemRequest = requestService.create(user.getId(), itemRequest1);

        List<RequestDto> itemRequests = requestService.findAll(user.getId(), 0, 10).stream().toList();

        assertThat(itemRequests).hasSize(1);
        assertThat(itemRequests.getFirst().getId()).isEqualTo(itemRequest.getId());
        assertThat(itemRequests.getFirst().getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(itemRequests.getFirst().getRequesterName()).isEqualTo(itemRequest.getRequesterName());
    }
}
