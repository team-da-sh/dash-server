package be.dash.dashserver.database.core.reservation;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import be.dash.dashserver.core.domain.reservation.ReservationStatus;

public interface ReservationJpaRepository extends JpaRepository<ReservationJpaEntity, Long> {

    boolean existsByMemberIdAndLessonId(Long memberId, Long lessonId);

    int countByMemberId(Long memberId);

    @Query("SELECT r FROM ReservationJpaEntity r " +
            "WHERE r.memberId = :memberId " +
            "AND (:status IS NULL OR r.status = :status) " +
            "ORDER BY r.createdAt DESC")
    List<ReservationJpaEntity> findByMemberIdAndStatusOrderByCreatedAtDesc(
            @Param("memberId") Long memberId,
            @Param("status") ReservationStatus status);

    List<ReservationJpaEntity> findAllByLessonIdOrderByCreatedAtDesc(Long lessonId);

    @Query("select count(r) from ReservationJpaEntity r join LessonJpaEntity l on r.lessonId = l.id " +
            "where r.memberId = :memberId and l.startDateTime > current_timestamp")
    int countUpcomingReservationsByMemberId(Long memberId);

    @Query("select count(r) from ReservationJpaEntity r join LessonJpaEntity l on r.lessonId = l.id " +
            "where r.memberId = :memberId and l.startDateTime <= current_timestamp and l.endDateTime >= current_timestamp")
    int countOngoingReservationsByMemberId(Long memberId);

    @Query("select count(r) from ReservationJpaEntity r join LessonJpaEntity l on r.lessonId = l.id " +
            "where r.memberId = :memberId and l.endDateTime < current_timestamp")
    int countPastReservationsByMemberId(Long memberId);

    @Modifying
    @Query("UPDATE ReservationJpaEntity r SET r.status = :status WHERE r.id = :reservationId")
    int updateStatusById(@Param("reservationId") Long reservationId, @Param("status") ReservationStatus status);

    List<ReservationJpaEntity> findByStatus(ReservationStatus status);

    List<ReservationJpaEntity> findByStatusAndCreatedAtBetween(ReservationStatus status, LocalDateTime start, LocalDateTime end);
}
