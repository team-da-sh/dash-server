package be.dash.dashserver.database.core.reservation;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;
import be.dash.dashserver.core.domain.reservation.Reservation;
import be.dash.dashserver.core.domain.reservation.ReservationStatus;
import be.dash.dashserver.core.domain.reservation.Reservations;
import be.dash.dashserver.core.domain.reservation.service.ReservationRepository;
import be.dash.dashserver.core.exception.NotFoundException;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryAdapter implements ReservationRepository {

    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public boolean existsApproveReservation(long memberId, long lessonId) {
        List<ReservationJpaEntity> reservations = reservationJpaRepository.findAllByMemberIdAndLessonIdOrderByCreatedAtDesc(memberId, lessonId);
        return reservations.stream()
                .anyMatch(ReservationJpaEntity::isStatusApprove);
    }

    @Override
    public Reservations findAllByMemberIdAndStatus(long memberId, ReservationStatus status) {
        return new Reservations(reservationJpaRepository.findByMemberIdAndStatusOrderByCreatedAtDesc(memberId, status).stream()
                .map(ReservationJpaEntity::toDomain).toList());
    }

    @Override
    public Reservation findById(long reservationId) {
        return reservationJpaRepository.findById(reservationId).map(ReservationJpaEntity::toDomain)
                .orElseThrow(() -> new NotFoundException("예약을 찾을 수 없습니다."));
    }

    @Override
    public Reservations findAllByMemberId(long memberId) {
        return new Reservations(reservationJpaRepository.findAllByMemberId(memberId)
                .stream().map(ReservationJpaEntity::toDomain).toList());
    }

    @Override
    public Reservations findAllByLessonIdAndReservationStatusOrderByCreatedAtDesc(Long lessonId, List<ReservationStatus> reservationStatusList) {
        return new Reservations(reservationJpaRepository.findAllByLessonIdOrderByCreatedAtAsc(lessonId).stream()
                .filter(reservation -> reservationStatusList.contains(reservation.getStatus()))
                .map(ReservationJpaEntity::toDomain)
                .toList());
    }

    @Override
    public int countUpcomingReservationsByMemberId(Long memberId, LocalDateTime now) {
        return reservationJpaRepository.countUpcomingReservationsByMemberId(memberId, now);
    }

    @Override
    public int countOngoingReservationsByMemberId(Long memberId, LocalDateTime now) {
        return reservationJpaRepository.countOngoingReservationsByMemberId(memberId, now);
    }

    @Override
    public int countPastReservationsByMemberId(Long memberId, LocalDateTime now) {
        return reservationJpaRepository.countPastReservationsByMemberId(memberId, now);
    }

    @Override
    public void pendingApprove(Long reservationId) {
        reservationJpaRepository.updateStatusById(reservationId, ReservationStatus.PENDING_APPROVAL, LocalDateTime.now());
    }

    @Override
    public void approve(Long reservationId) {
        reservationJpaRepository.updateStatusById(reservationId, ReservationStatus.APPROVED, LocalDateTime.now());
    }

    @Override
    public void pendingCancel(Long reservationId) {
        reservationJpaRepository.updateStatusById(reservationId, ReservationStatus.PENDING_CANCELLATION, LocalDateTime.now());
    }

    @Override
    public void cancel(Long reservationId) {
        reservationJpaRepository.updateStatusById(reservationId, ReservationStatus.CANCELLED, LocalDateTime.now());
    }

    @Override
    public void inProgress(Long reservationId) {
        reservationJpaRepository.updateStatusById(reservationId, ReservationStatus.IN_PROGRESS, LocalDateTime.now());
    }

    @Override
    public void completed(Long reservationId) {
        reservationJpaRepository.updateStatusById(reservationId, ReservationStatus.COMPLETED, LocalDateTime.now());
    }

    @Override
    public List<Reservation> findByStatus(ReservationStatus status) {
        return reservationJpaRepository.findByStatus(status).stream().map(ReservationJpaEntity::toDomain).toList();
    }

    @Override
    public List<Reservation> findByStatusAndCreatedAtBetween(ReservationStatus status, LocalDateTime start, LocalDateTime end) {
        return reservationJpaRepository.findByStatusAndCreatedAtBetween(status, start, end).stream()
                .map(ReservationJpaEntity::toDomain).toList();
    }

    @Override
    public Reservations findAllByLessonIds(List<Long> teacherLessonIds) {
        return new Reservations(reservationJpaRepository.findAllByLessonIdIn(teacherLessonIds).stream()
                .map(ReservationJpaEntity::toDomain).toList());
    }

    @Override
    public long save(long memberId, long lessonId) {
        ReservationJpaEntity reservationJpaEntity = new ReservationJpaEntity(lessonId, memberId, ReservationStatus.PENDING_APPROVAL);
        reservationJpaRepository.save(reservationJpaEntity);
        return reservationJpaEntity.getId();
    }
}
