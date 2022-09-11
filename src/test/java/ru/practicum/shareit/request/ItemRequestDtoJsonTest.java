package ru.practicum.shareit.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonTest
public class ItemRequestDtoJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDto() throws Exception {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("itemRequestDescription")
                .requesterId(1L)
                .created(LocalDateTime.of(2021, 11, 12, 10, 00, 00))
                .items(new ArrayList<>())
                .build();
        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("itemRequestDescription");
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.requesterId")
                .isEqualTo(1);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2021-11-12T10:00:00");
        Assertions.assertThat(result).extractingJsonPathArrayValue("items")
                .isEqualTo(List.of());
    }
}