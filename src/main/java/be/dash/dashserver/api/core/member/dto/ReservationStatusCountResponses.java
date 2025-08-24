package be.dash.dashserver.api.core.member.dto;

import java.util.List;
import be.dash.dashserver.core.domain.reservation.Reservations;

import static be.dash.dashserver.core.domain.reservation.ReservationStatus.APPROVED;
import static be.dash.dashserver.core.domain.reservation.ReservationStatus.CANCELLED;
import static be.dash.dashserver.core.domain.reservation.ReservationStatus.PENDING_APPROVAL;
import static be.dash.dashserver.core.domain.reservation.ReservationStatus.PENDING_CANCELLATION;

public record ReservationStatusCountResponses(List<ReservationStatusCount> reservationStatusCounts) {
    private static final String ALL_STATUS = "ALL";

    public static ReservationStatusCountResponses from(Reservations reservations) {
        ReservationStatusCount all = new ReservationStatusCount(ALL_STATUS, reservations.getSize());
        ReservationStatusCount pendingApproval = new ReservationStatusCount(PENDING_APPROVAL.name(), reservations.getReservations()
                .stream()
                .filter(reservation -> reservation.getReservationStatus() == PENDING_APPROVAL).count());
        ReservationStatusCount approved = new ReservationStatusCount(APPROVED.name(), reservations.getReservations()
                .stream()
                .filter(reservation -> reservation.getReservationStatus() == APPROVED).count());
        ReservationStatusCount pendingCancellation = new ReservationStatusCount(PENDING_CANCELLATION.name(), reservations.getReservations()
                .stream()
                .filter(reservation -> reservation.getReservationStatus() == PENDING_CANCELLATION).count());
        ReservationStatusCount cancelled = new ReservationStatusCount(CANCELLED.name(), reservations.getReservations()
                .stream()
                .filter(reservation -> reservation.getReservationStatus() == CANCELLED).count());
        return new ReservationStatusCountResponses(List.of(all, pendingApproval, approved, pendingCancellation, cancelled));
    }
}
