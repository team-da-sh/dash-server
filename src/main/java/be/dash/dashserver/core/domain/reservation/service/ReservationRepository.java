package be.dash.dashserver.core.domain.reservation.service;

import be.dash.dashserver.core.domain.reservation.Reservation;
import be.dash.dashserver.core.domain.reservation.Reservations;

public interface ReservationRepository {

    boolean existsByMemberIdAndLessonId(long memberId, long lessonId);

    long save(long memberId, long lessonId);

    Reservations findAllByMemberId(long memberId);

    Reservation findById(long reservationId);

    Reservations findAllByLessonIdOrderByCreatedAtDesc(Long lessonId);

    int countUpcomingReservationsByMemberId(Long memberId);

    int countOngoingReservationsByMemberId(Long memberId);

    int countPastReservationsByMemberId(Long memberId);
}
