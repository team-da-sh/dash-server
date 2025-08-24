package be.dash.dashserver.core.domain.member.service;

import java.time.LocalDateTime;
import be.dash.dashserver.core.domain.common.AttendStatus;
import be.dash.dashserver.core.domain.lesson.Lesson;
import be.dash.dashserver.core.domain.reservation.Reservation;
import be.dash.dashserver.core.domain.reservation.ReservationStatus;
import be.dash.dashserver.core.domain.reservation.Reservations;

public record ReservationResult(
        long lessonId,
        long reservationId,
        String name,
        String imageUrl,
        String genre,
        String level,
        String location,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        int dDay,
        AttendStatus attendStatus,
        ReservationStatus reservationStatus,
        LocalDateTime createdAt
) {
    public static ReservationResult of(Lesson lesson, Reservations reservations) {
        Reservation reservation = reservations.findReservationByLessonId(lesson.getId());
        return new ReservationResult(//첫번째 레슨과 reservations에 대해 다음을 한다.
                lesson.getId(),
                reservation.getId(),
                lesson.getName(),
                lesson.getRepresentativeImageUrl(),
                lesson.getGenre().name(),
                lesson.getLevel().name(),
                lesson.getLocationName(),
                lesson.getStartTime(),
                lesson.getEndTime(),
                lesson.calculateDDay(),
                AttendStatus.calculateAttendStatus(lesson.getStartTime(), lesson.getEndTime()),
                reservation.getReservationStatus(),
                reservation.getCreatedAt()
        );
    }

}
