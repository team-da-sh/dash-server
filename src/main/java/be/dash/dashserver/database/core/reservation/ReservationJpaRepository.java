package be.dash.dashserver.database.core.reservation;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReservationJpaRepository extends JpaRepository<ReservationJpaEntity, Long> {

    boolean existsByMemberIdAndLessonId(Long memberId, Long lessonId);

    int countByMemberId(Long memberId);

    List<ReservationJpaEntity> findAllByMemberId(long memberId);

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
}
