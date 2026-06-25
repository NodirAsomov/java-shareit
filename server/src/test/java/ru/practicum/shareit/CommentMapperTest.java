package ru.practicum.shareit;


import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    @Test
    void toDto_shouldMapCorrectly() {
        User author = new User();
        author.setId(1L);
        author.setName("John");

        Comment comment = new Comment();
        comment.setId(10L);
        comment.setText("great item");
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        CommentDto dto = CommentMapper.toDto(comment);

        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals("great item", dto.getText());
        assertEquals("John", dto.getAuthorName());
        assertNotNull(dto.getCreated());
    }

    @Test
    void toEntity_shouldMapCorrectly() {
        CommentDto dto = CommentDto.builder()
                .text("hello")
                .build();

        Comment comment = CommentMapper.toEntity(dto);

        assertNotNull(comment);
        assertEquals("hello", comment.getText());
    }
}