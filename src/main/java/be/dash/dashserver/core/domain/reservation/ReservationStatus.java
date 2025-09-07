package be.dash.dashserver.core.domain.reservation;

public enum ReservationStatus {

    PENDING_APPROVAL("승인대기"),
    APPROVED("승인완료"),
    PENDING_CANCELLATION("취소대기"),
    CANCELLED("취소완료"),
    IN_PROGRESS("수강중"),
    COMPLETED("수강완료");

    private final String description;

    ReservationStatus(String description) {
        this.description = description;
    }

    public static ReservationStatus match(boolean deposited) {
        if (deposited) {
            return PENDING_CANCELLATION;
        }
        return CANCELLED;
    }
}
