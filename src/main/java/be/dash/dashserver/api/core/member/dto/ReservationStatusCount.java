package be.dash.dashserver.api.core.member.dto;

public record ReservationStatusCount(
        String reservationStatus,
        long count
) {
}
