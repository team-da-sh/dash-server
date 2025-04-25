package be.dash.dashserver.database.core.reservation;

import org.springframework.stereotype.Repository;
import be.dash.dashserver.core.domain.reservation.Reservation;
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
        ReservationJpaEntity reservationJpaEntity = new ReservationJpaEntity(lessonId, memberId);
        reservationJpaRepository.save(reservationJpaEntity);
        return reservationJpaEntity.getId();
    }
}
