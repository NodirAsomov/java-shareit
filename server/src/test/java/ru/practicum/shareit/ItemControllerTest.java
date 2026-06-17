package ru.practicum.shareit;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService service;

    @Test
    void createItem_shouldReturnOk() throws Exception {
        ItemDto dto = new ItemDto();

        when(service.create(anyLong(), any())).thenReturn(dto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateItem_shouldReturnOk() throws Exception {
        ItemDto dto = new ItemDto();

        when(service.update(anyLong(), anyLong(), any())).thenReturn(dto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void getItem_shouldReturnOk() throws Exception {
        when(service.get(anyLong())).thenReturn(new ItemDto());

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getOwnerItems_shouldReturnOk() throws Exception {
        when(service.getOwnerItems(anyLong())).thenReturn(List.of());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    void search_shouldReturnOk() throws Exception {
        when(service.search(anyString())).thenReturn(List.of());

        mockMvc.perform(get("/items/search?text=drill"))
                .andExpect(status().isOk());
    }

    @Test
    void addComment_shouldReturnOk() throws Exception {
        CommentDto dto = new CommentDto();

        when(service.addComment(anyLong(), anyLong(), any())).thenReturn(dto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}