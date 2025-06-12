package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.RequestDto;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestDtoJsonTest {
    private final JacksonTester<RequestDto> json;

    @Test
    void testSerialize() throws Exception {
        RequestDto requestDto = RequestDto.builder()
                .id(1L)
                .description("description")
                .requesterName("requesterName")
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        JsonContent<RequestDto> result = json.write(requestDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.requesterName");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).hasJsonPathValue("$.created");
        assertThat(result).hasJsonPath("$.items");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(requestDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(requestDto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.requesterName").isEqualTo(requestDto.getRequesterName());
        assertThat(result).extractingJsonPathArrayValue("$.items");
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonString = "{ \"id\": 1, \"description\": \"description\", \"requesterName\": \"requesterName\", " +
                            "\"created\": \"2023-10-01T10:00:00\", \"items\": [] }";

        RequestDto requestDto = this.json.parse(jsonString).getObject();

        assertThat(requestDto).isNotNull();
        assertThat(requestDto.getId()).isEqualTo(1L);
        assertThat(requestDto.getDescription()).isEqualTo("description");
        assertThat(requestDto.getRequesterName()).isEqualTo("requesterName");
        assertThat(requestDto.getCreated()).isEqualTo(LocalDateTime.parse("2023-10-01T10:00:00")); // Убедитесь, что это совпадает
        assertThat(requestDto.getItems()).isEmpty();
    }
}
