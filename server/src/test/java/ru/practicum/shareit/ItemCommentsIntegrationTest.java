package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ItemCommentsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void getItem_shouldReturnSavedComments() throws Exception {
        User owner = userRepository.save(user("owner", "owner-comment-test@mail.com"));
        User booker = userRepository.save(user("booker", "booker-comment-test@mail.com"));

        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Power drill");
        item.setAvailable(true);
        item.setOwner(owner);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setItem(savedItem);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setCreated(LocalDateTime.now().minusDays(3));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        mockMvc.perform(post("/items/{itemId}/comment", savedItem.getId())
                        .header("X-Sharer-User-Id", booker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Works well\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/items/{itemId}", savedItem.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments", hasSize(1)))
                .andExpect(jsonPath("$.comments[0].text").value("Works well"));
    }

    private User user(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}
