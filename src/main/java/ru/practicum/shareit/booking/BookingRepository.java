package ru.practicum.shareit.booking;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {


    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);


    @Query("""
                select b from Booking b
                where b.booker.id = :bookerId
                and b.start < CURRENT_TIMESTAMP
                and b.end > CURRENT_TIMESTAMP
                order by b.start desc
            """)
    List<Booking> findCurrentByBooker(Long bookerId);


    @Query("""
                select b from Booking b
                where b.booker.id = :bookerId
                and b.end < CURRENT_TIMESTAMP
                order by b.start desc
            """)
    List<Booking> findPastByBooker(Long bookerId);


    @Query("""
                select b from Booking b
                where b.booker.id = :bookerId
                and b.start > CURRENT_TIMESTAMP
                order by b.start desc
            """)
    List<Booking> findFutureByBooker(Long bookerId);


    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);


    @Query("""
                select b from Booking b
                where b.item.owner.id = :ownerId
                and b.start < CURRENT_TIMESTAMP
                and b.end > CURRENT_TIMESTAMP
                order by b.start desc
            """)
    List<Booking> findCurrentByOwner(Long ownerId);


    @Query("""
                select b from Booking b
                where b.item.owner.id = :ownerId
                and b.end < CURRENT_TIMESTAMP
                order by b.start desc
            """)
    List<Booking> findPastByOwner(Long ownerId);


    @Query("""
                select b from Booking b
                where b.item.owner.id = :ownerId
                and b.start > CURRENT_TIMESTAMP
                order by b.start desc
            """)
    List<Booking> findFutureByOwner(Long ownerId);


    boolean existsByItemIdAndBookerIdAndStatusAndEndBefore(
            Long itemId,
            Long bookerId,
            BookingStatus status,
            LocalDateTime end
    );

    List<Booking> findByItemIdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime time);

    List<Booking> findByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime time);

    List<Booking> findByItemIdInAndStatusOrderByStartAsc(
            List<Long> itemIds,
            BookingStatus status
    );
}