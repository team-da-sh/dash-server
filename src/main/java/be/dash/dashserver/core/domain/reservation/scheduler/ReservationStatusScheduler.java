package be.dash.dashserver.core.domain.reservation.scheduler;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import be.dash.dashserver.core.domain.lesson.Lesson;
import be.dash.dashserver.core.domain.lesson.service.LessonRepository;
import be.dash.dashserver.core.domain.reservation.ReservationStatus;
import be.dash.dashserver.core.domain.reservation.service.ReservationRepository;
import be.dash.dashserver.database.core.reservation.ReservationJpaEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationStatusScheduler {

    private final ReservationRepository reservationRepository;
    private final LessonRepository lessonRepository;

    @Scheduled(cron = "0 */10 * * * *")
    public void updateReservationStatuses() {
        LocalDateTime now = LocalDateTime.now();
        List<ReservationJpaEntity> reservations = reservationRepository.findByStatus(ReservationStatus.APPROVED);

        for (ReservationJpaEntity reservation : reservations) {
            Lesson lesson = lessonRepository.findLessonsById(reservation.getLessonId());
            LocalDateTime start = lesson.getStartTime();
            LocalDateTime end = lesson.getEndTime();

            if (now.isAfter(end)) {
                reservationRepository.completed(reservation.getId());
            } else if (now.isAfter(start) && now.isBefore(end)) {
                reservationRepository.inProgress(reservation.getId());
            }
        }
        log.info("✅ 예약 상태 자동 갱신 완료 (건수: {})", reservations.size());
    }
}
