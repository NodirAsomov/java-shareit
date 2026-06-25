package ru.practicum.shareit;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService service;

    @Test
    void createUser_shouldReturnOk() throws Exception {
        when(service.create(any())).thenReturn(new UserDto());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserDto())))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser_shouldReturnOk() throws Exception {
        when(service.update(anyLong(), any())).thenReturn(new UserDto());

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserDto())))
                .andExpect(status().isOk());
    }

    @Test
    void getUser_shouldReturnOk() throws Exception {
        when(service.get(anyLong())).thenReturn(new UserDto());

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}