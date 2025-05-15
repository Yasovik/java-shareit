package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.marker.Marker;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDto {
    @NotBlank(message = "Имя не может быть пустым", groups = Marker.OnCreate.class)
    private String name;
    @NotBlank(message = "Описание не может быть пустым", groups = Marker.OnCreate.class)
    @Size(max = 300, message = "Длина описания не должна превышать 300 символов")
    private String description;
    @NotNull(message = "Поле доступности не можеть быть пыстым", groups = Marker.OnCreate.class)
    private Boolean available;
    private Integer requestId;
}