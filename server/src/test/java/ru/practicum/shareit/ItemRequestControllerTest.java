package ru.practicum.shareit;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService service;

    @Test
    void create_shouldReturnOk() throws Exception {
        when(service.create(anyLong(), any())).thenReturn(org.mockito.Mockito.mock(ItemRequestResponseDto.class));


        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ItemRequestDto())))
                .andExpect(status().isOk());
    }

    @Test
    void getOwn_shouldReturnOk() throws Exception {
        when(service.getOwn(anyLong())).thenReturn(List.of());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    void getAll_shouldReturnOk() throws Exception {
        when(service.getAll(anyLong(), anyInt(), anyInt())).thenReturn(List.of());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    void getById_shouldReturnOk() throws Exception {
        when(service.getById(anyLong(), anyLong()))
                .thenReturn(org.mockito.Mockito.mock(ItemRequestResponseDto.class));

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }
}
