package be.dash.dashserver.database.core.reservation;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationJpaRepository extends JpaRepository<ReservationJpaEntity, Long> {

    boolean existsByMemberIdAndLessonId(Long memberId, Long lessonId);

    int countByMemberId(Long memberId);

    List<ReservationJpaEntity> findAllByMemberId(long memberId);

    List<ReservationJpaEntity> findAllByLessonIdOrderByCreatedAtDesc(Long lessonId);
}
