package be.dash.dashserver.database.core.reservation;

import org.springframework.stereotype.Repository;
import be.dash.dashserver.core.domain.reservation.Reservation;
import be.dash.dashserver.core.domain.reservation.ReservationStatus;
import be.dash.dashserver.core.domain.reservation.Reservations;
import be.dash.dashserver.core.domain.reservation.command.CancelReservationCommand;
import be.dash.dashserver.core.domain.reservation.service.ReservationRepository;
import be.dash.dashserver.core.exception.ForbiddenException;
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
    public Reservations findAllByMemberId(long memberId) {
        return new Reservations(reservationJpaRepository.findAllByMemberId(memberId).stream()
                .map(ReservationJpaEntity::toDomain).toList());
    }

    @Override
    public Reservation findById(long reservationId) {
        return reservationJpaRepository.findById(reservationId).map(ReservationJpaEntity::toDomain)
                .orElseThrow(() -> new NotFoundException("예약을 찾을 수 없습니다."));
    }

    @Override
    public Reservations findAllByLessonIdOrderByCreatedAtDesc(Long lessonId) {
        return new Reservations(reservationJpaRepository.findAllByLessonIdOrderByCreatedAtDesc(lessonId).stream()
                .map(ReservationJpaEntity::toDomain).toList());
    }

    @Override
    public void cancel(long memberId, long reservationId, CancelReservationCommand cancelReservationCommand) {
        Reservation reservation = reservationJpaRepository.findById(reservationId).map(ReservationJpaEntity::toDomain)
                .orElseThrow(() -> new NotFoundException("예약을 찾을 수 없습니다."));
        if (!reservation.ownBy(memberId)) {
            throw new ForbiddenException("예약을 취소할 권한이 없습니다.");
        }
        reservation.changeStatus(cancelReservationCommand.reservationStatus());
        // 문자 발송, 아직 수강생의 계좌정보를 저장할지는 모르겠음.
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
    public long save(long memberId, long lessonId) {
        ReservationJpaEntity reservationJpaEntity = new ReservationJpaEntity(lessonId, memberId, ReservationStatus.PENDING_APPROVAL);
        reservationJpaRepository.save(reservationJpaEntity);
        return reservationJpaEntity.getId();
    }
}
