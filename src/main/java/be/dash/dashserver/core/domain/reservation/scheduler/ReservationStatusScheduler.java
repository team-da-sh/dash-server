package be.dash.dashserver.core.domain.reservation.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import be.dash.dashserver.core.domain.lesson.Lesson;
import be.dash.dashserver.core.domain.lesson.service.LessonRepository;
import be.dash.dashserver.core.domain.member.Member;
import be.dash.dashserver.core.domain.member.service.MemberRepository;
import be.dash.dashserver.core.domain.reservation.Reservation;
import be.dash.dashserver.core.domain.reservation.ReservationStatus;
import be.dash.dashserver.core.domain.reservation.service.ReservationRepository;
import be.dash.dashserver.core.external.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationStatusScheduler {

    private final ReservationRepository reservationRepository;
    private final LessonRepository lessonRepository;
    private final MemberRepository memberRepository;
    private final MessageSender messageSender;

    @Scheduled(cron = "0 */10 * * * *")
    public void updateReservationStatuses() {
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> reservations = reservationRepository.findByStatus(ReservationStatus.APPROVED);

        for (Reservation reservation : reservations) {
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

    @Scheduled(cron = "0 0 0 * * *")
    public void cancelOutdatedPendingReservations() {
        LocalDate targetDate = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(2);
        LocalDateTime startOfYesterday = targetDate.atStartOfDay();
        LocalDateTime endOfYesterday = targetDate.atTime(LocalTime.MAX);

        List<Reservation> outdatedPending = reservationRepository
                .findByStatusAndCreatedAtBetween(ReservationStatus.PENDING_APPROVAL, startOfYesterday, endOfYesterday);

        for (Reservation reservation : outdatedPending) {
            Member member = memberRepository.findById(reservation.getMemberId());
            Lesson lesson = lessonRepository.findLessonsById(reservation.getLessonId());
            reservationRepository.cancel(reservation.getId());
            messageSender.sendCancelledBySystem(member.getPhoneNumber(), member.getName(), lesson.getTeacher()
                    .getNickname(), lesson.getName());
        }

        log.info("🚫 어제 생성된 미승인 예약 자동 취소 (건수: {})", outdatedPending.size());
    }
}
