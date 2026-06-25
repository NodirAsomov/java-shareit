package ru.practicum.shareit;


import org.springframework.boot.test.context.SpringBootTest;


import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class ShareItTests {

    @Test
    void contextLoads() {
        assertDoesNotThrow(() -> {
        });
    }
}

