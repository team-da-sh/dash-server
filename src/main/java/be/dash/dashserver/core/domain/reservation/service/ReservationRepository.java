package be.dash.dashserver.core.domain.reservation.service;

import java.util.List;
import be.dash.dashserver.core.domain.reservation.Reservation;
import be.dash.dashserver.core.domain.reservation.ReservationStatus;
import be.dash.dashserver.core.domain.reservation.Reservations;
import be.dash.dashserver.core.domain.reservation.command.CancelReservationCommand;

public interface ReservationRepository {

    boolean existsByMemberIdAndLessonId(long memberId, long lessonId);

    long save(long memberId, long lessonId);

    Reservations findAllByMemberIdAndStatus(long memberId, ReservationStatus status);

    Reservation findById(long reservationId);

    Reservations findAllByLessonIdAndReservationStatusOrderByCreatedAtDesc(Long lessonId, List<ReservationStatus> reservationStatusList);

    void cancel(long memberId, long reservationId, CancelReservationCommand cancelReservationCommand);

    int countUpcomingReservationsByMemberId(Long memberId);

    int countOngoingReservationsByMemberId(Long memberId);

    int countPastReservationsByMemberId(Long memberId);
}
