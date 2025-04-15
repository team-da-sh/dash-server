package be.dash.dashserver.database.core.reservation;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationJpaRepository extends JpaRepository<ReservationJpaEntity, Long> {

    boolean existsByMemberIdAndLessonId(Long memberId, Long lessonId);

    int countByStudentId(Long studentId);

    List<ReservationJpaEntity> findAllByStudentId(long studentId);

    List<ReservationJpaEntity> findAllByLessonIdOrderByCreatedAtDesc(Long lessonId);
}
