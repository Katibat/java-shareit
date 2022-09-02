package ru.practicum.shareit.booking;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

@JsonTest
public class BookingDtoJsonTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testItemRequestDto() throws Exception {
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 12, 11, 19, 00,00))
                .end(LocalDateTime.of(2022, 12, 20, 22, 00,00))
                .item(null)
                .booker(null)
                .status(BookingStatus.WAITING)
                .build();
        JsonContent<BookingDto> result = json.write(bookingDto);
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2022-12-11T19:00:00");
        Assertions.assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2022-12-20T22:00:00");
        Assertions.assertThat(result).extractingJsonPathValue("$.item")
                .isEqualTo(null);
        Assertions.assertThat(result).extractingJsonPathValue("$.booker")
                .isEqualTo(null);
        Assertions.assertThat(result).extractingJsonPathStringValue("status")
                .isEqualTo("WAITING");
    }
}