package be.dash.dashserver.api.core.member.dto;

import java.util.List;
import be.dash.dashserver.core.domain.reservation.Reservations;

import static be.dash.dashserver.core.domain.reservation.ReservationStatus.APPROVED;
import static be.dash.dashserver.core.domain.reservation.ReservationStatus.CANCELLED;
import static be.dash.dashserver.core.domain.reservation.ReservationStatus.PENDING_APPROVAL;
import static be.dash.dashserver.core.domain.reservation.ReservationStatus.PENDING_CANCELLATION;

public record ReservationStatusCountResponses(List<StatusCount> reservationStatusCounts) {
    private static final String ALL_STATUS = "ALL";

    public static ReservationStatusCountResponses from(Reservations reservations) {
        StatusCount all = new StatusCount(ALL_STATUS, reservations.getSize());
        StatusCount pendingApproval = new StatusCount(PENDING_APPROVAL.name(), reservations.getReservations()
                .stream()
                .filter(reservation -> reservation.getReservationStatus() == PENDING_APPROVAL).count());
        StatusCount approved = new StatusCount(APPROVED.name(), reservations.getReservations()
                .stream()
                .filter(reservation -> reservation.getReservationStatus() == APPROVED).count());
        StatusCount pendingCancellation = new StatusCount(PENDING_CANCELLATION.name(), reservations.getReservations()
                .stream()
                .filter(reservation -> reservation.getReservationStatus() == PENDING_CANCELLATION).count());
        StatusCount cancelled = new StatusCount(CANCELLED.name(), reservations.getReservations()
                .stream()
                .filter(reservation -> reservation.getReservationStatus() == CANCELLED).count());
        return new ReservationStatusCountResponses(List.of(all, pendingApproval, approved, pendingCancellation, cancelled));
    }
}
