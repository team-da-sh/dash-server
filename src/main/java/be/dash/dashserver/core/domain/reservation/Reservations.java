package be.dash.dashserver.core.domain.reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import be.dash.dashserver.core.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Reservations {
    private List<Reservation> reservations;

    public Reservation findReservationByLessonId(long id) {
        return reservations.stream().filter(reservation -> reservation.getLessonId() == id)
                .findFirst().orElseThrow(() -> new NotFoundException("해당하는 예약을 찾을 수 없습니다."));
    }

    public int getSize() {
        return reservations.size();
    }

    public Set<Long> getLessonIds() {
        return reservations.stream().map(Reservation::getLessonId).collect(Collectors.toSet());
    }

    public List<Long> getMemberIds() {
        return reservations.stream().map(Reservation::getMemberId).toList();
    }

    public List<LocalDateTime> getCreatedAt() {
        return reservations.stream()
                .map(Reservation::getCreatedAt)
                .toList();
    }
}
