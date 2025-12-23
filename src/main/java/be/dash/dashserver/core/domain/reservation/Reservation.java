package be.dash.dashserver.core.domain.reservation;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Reservation {
    private final long id;
    private final long lessonId;
    private final long memberId;
    private ReservationStatus reservationStatus;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public boolean ownBy(long memberId) {
        return this.memberId == memberId;
    }
}
