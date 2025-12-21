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

    List<ReservationJpaEntity> findAllByLessonIdOrderByCreatedAtAsc(Long lessonId);

    @Query("""
    SELECT COUNT(r)
    FROM ReservationJpaEntity r
    JOIN LessonJpaEntity l ON r.lessonId = l.id
    WHERE r.memberId = :memberId
      AND r.status = be.dash.dashserver.core.domain.reservation.ReservationStatus.APPROVED
      AND l.startDateTime > CURRENT_TIMESTAMP""")
    int countUpcomingReservationsByMemberId(Long memberId);

    @Query("""
    SELECT COUNT(r)
    FROM ReservationJpaEntity r
    JOIN LessonJpaEntity l ON r.lessonId = l.id
    WHERE r.memberId = :memberId
      AND r.status = be.dash.dashserver.core.domain.reservation.ReservationStatus.IN_PROGRESS
      AND l.startDateTime <= CURRENT_TIMESTAMP
      AND l.endDateTime >= CURRENT_TIMESTAMP""")
    int countOngoingReservationsByMemberId(Long memberId);

    @Query("""
    SELECT COUNT(r)
    FROM ReservationJpaEntity r
    JOIN LessonJpaEntity l ON r.lessonId = l.id
    WHERE r.memberId = :memberId
      AND r.status = be.dash.dashserver.core.domain.reservation.ReservationStatus.COMPLETED
      AND l.endDateTime < CURRENT_TIMESTAMP""")
    int countPastReservationsByMemberId(Long memberId);

    @Modifying
    @Query("UPDATE ReservationJpaEntity r SET r.status = :status WHERE r.id = :reservationId")
    int updateStatusById(@Param("reservationId") Long reservationId, @Param("status") ReservationStatus status);

    List<ReservationJpaEntity> findByStatus(ReservationStatus status);

    List<ReservationJpaEntity> findByStatusAndCreatedAtBetween(ReservationStatus status, LocalDateTime start, LocalDateTime end);

    ReservationJpaEntity findByMemberIdAndLessonId(Long memberId, Long lessonId);

    List<ReservationJpaEntity> findAllByMemberId(Long memberId);

    @Query("select r from ReservationJpaEntity r where r.lessonId in :lessonIds")
    List<ReservationJpaEntity> findAllByLessonIdIn(List<Long> lessonIds);
}
