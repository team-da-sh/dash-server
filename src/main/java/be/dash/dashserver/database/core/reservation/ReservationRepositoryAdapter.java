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
    public boolean existsByMemberIdAndLessonId(long memberId, long lessonId) {
        return reservationJpaRepository.existsByMemberIdAndLessonId(memberId, lessonId);
    }

    @Override
    public Reservations findAllByMemberIdAndStatus(long memberId, ReservationStatus status) {
        return new Reservations(reservationJpaRepository.findAllByMemberIdAndStatusNullable(memberId, status).stream()
                .map(ReservationJpaEntity::toDomain).toList());
    }

    @Override
    public Reservation findById(long reservationId) {
        return reservationJpaRepository.findById(reservationId).map(ReservationJpaEntity::toDomain)
                .orElseThrow(() -> new NotFoundException("예약을 찾을 수 없습니다."));
    }

    @Override
    public Reservations findAllByLessonIdAndReservationStatusOrderByCreatedAtDesc(Long lessonId, List<ReservationStatus> reservationStatusList) {
        return new Reservations(reservationJpaRepository.findAllByLessonIdOrderByCreatedAtDesc(lessonId).stream()
                .filter(reservation -> reservationStatusList.contains(reservation.getStatus()))
                .map(ReservationJpaEntity::toDomain)
                .toList());
    }

    @Override
    public int countUpcomingReservationsByMemberId(Long memberId) {
        return reservationJpaRepository.countUpcomingReservationsByMemberId(memberId);
    }

    @Override
    public int countOngoingReservationsByMemberId(Long memberId) {
        return reservationJpaRepository.countOngoingReservationsByMemberId(memberId);
    }

    @Override
    public int countPastReservationsByMemberId(Long memberId) {
        return reservationJpaRepository.countPastReservationsByMemberId(memberId);
    }

    @Override
    public void pendingApprove(Long reservationId) {
        reservationJpaRepository.updateStatusById(reservationId, ReservationStatus.PENDING_APPROVAL);
    }

    @Override
    public void approve(Long reservationId) {
        reservationJpaRepository.updateStatusById(reservationId, ReservationStatus.APPROVED);
    }

    @Override
    public void pendingCancel(Long reservationId) {
        reservationJpaRepository.updateStatusById(reservationId, ReservationStatus.PENDING_CANCELLATION);
    }

    @Override
    public void cancel(Long reservationId) {
        reservationJpaRepository.updateStatusById(reservationId, ReservationStatus.CANCELLED);
    }

    @Override
    public void inProgress(Long reservationId) {
        reservationJpaRepository.updateStatusById(reservationId, ReservationStatus.IN_PROGRESS);
    }

    @Override
    public void completed(Long reservationId) {
        reservationJpaRepository.updateStatusById(reservationId, ReservationStatus.COMPLETED);
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
    public long save(long memberId, long lessonId) {
        ReservationJpaEntity reservationJpaEntity = new ReservationJpaEntity(lessonId, memberId, ReservationStatus.PENDING_APPROVAL);
        reservationJpaRepository.save(reservationJpaEntity);
        return reservationJpaEntity.getId();
    }
}
