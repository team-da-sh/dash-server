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
      AND l.startDateTime > :now""")
    int countUpcomingReservationsByMemberId(@Param("memberId") Long memberId, @Param("now") LocalDateTime now);

    @Query("""
    SELECT COUNT(r)
    FROM ReservationJpaEntity r
    JOIN LessonJpaEntity l ON r.lessonId = l.id
    WHERE r.memberId = :memberId
      AND r.status = be.dash.dashserver.core.domain.reservation.ReservationStatus.IN_PROGRESS
      AND l.startDateTime <= :now
      AND l.endDateTime >= :now""")
    int countOngoingReservationsByMemberId(@Param("memberId") Long memberId, @Param("now") LocalDateTime now);

    @Query("""
    SELECT COUNT(r)
    FROM ReservationJpaEntity r
    JOIN LessonJpaEntity l ON r.lessonId = l.id
    WHERE r.memberId = :memberId
      AND r.status = be.dash.dashserver.core.domain.reservation.ReservationStatus.COMPLETED
      AND l.endDateTime < :now""")
    int countPastReservationsByMemberId(@Param("memberId") Long memberId, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE ReservationJpaEntity r SET r.status = :status, r.updatedAt = :now WHERE r.id = :reservationId")
    int updateStatusById(@Param("reservationId") Long reservationId,
                         @Param("status") ReservationStatus status,
                         @Param("now") LocalDateTime now);

    List<ReservationJpaEntity> findByStatus(ReservationStatus status);

    List<ReservationJpaEntity> findByStatusAndCreatedAtBetween(ReservationStatus status, LocalDateTime start, LocalDateTime end);

    List<ReservationJpaEntity> findAllByMemberIdAndLessonIdOrderByCreatedAtDesc(Long memberId, Long lessonId);

    List<ReservationJpaEntity> findAllByMemberId(Long memberId);

    @Query("select r from ReservationJpaEntity r where r.lessonId in :lessonIds")
    List<ReservationJpaEntity> findAllByLessonIdIn(List<Long> lessonIds);
}
