package be.dash.dashserver.core.domain.reservation;

import java.util.List;
import lombok.Getter;

public enum ReservationStatusType {
    APPROVE("승인", List.of(ReservationStatus.APPROVED, ReservationStatus.PENDING_APPROVAL)),
    CANCEL("취소", List.of(ReservationStatus.CANCELLED, ReservationStatus.PENDING_CANCELLATION));

    private final String description;
    @Getter
    private final List<ReservationStatus> reservationStatuses;

    ReservationStatusType(String description, List<ReservationStatus> reservationStatuses) {
        this.description = description;
        this.reservationStatuses = reservationStatuses;
    }
}
