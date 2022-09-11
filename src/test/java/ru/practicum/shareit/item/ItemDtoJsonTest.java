package ru.practicum.shareit.item;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import java.util.ArrayList;
import java.util.List;

@JsonTest
public class ItemDtoJsonTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testItemDto() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("itemDescription")
                .available(true)
                .owner(null)
                .requestId(null)
                .lastBooking(null)
                .nextBooking(null)
                .comments(new ArrayList<>())
                .build();
        JsonContent<ItemDto> result = json.write(itemDto);
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo("item");
        Assertions.assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("itemDescription");
        Assertions.assertThat(result).extractingJsonPathValue("$.available")
                .isEqualTo(true);
        Assertions.assertThat(result).extractingJsonPathValue("$.owner")
                .isEqualTo(null);
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(null);
        Assertions.assertThat(result).extractingJsonPathValue("lastBooking")
                .isEqualTo(null);
        Assertions.assertThat(result).extractingJsonPathValue("nextBooking")
                .isEqualTo(null);
        Assertions.assertThat(result).extractingJsonPathArrayValue("comments")
                .isEqualTo(List.of());
    }
}