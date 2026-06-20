package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
            select c from Comment c
            where c.item.id = :itemId
            order by c.created desc
            """)
    List<Comment> findByItemIdOrderByCreatedDesc(Long itemId);

    @Query("""
            select c from Comment c
            where c.item.id in :itemIds
            order by c.created desc
            """)
    List<Comment> findByItemIdInOrderByCreatedDesc(List<Long> itemIds);
}


