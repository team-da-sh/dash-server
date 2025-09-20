package be.dash.dashserver.api.core.lesson.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public record LessonRoundResponse(LocalDateTime startDateTime, LocalDateTime endDateTime) {

    public static long calculateRemainingDays(LocalDateTime startDateTime) {
        return ChronoUnit.DAYS.between(LocalDate.now(), startDateTime.toLocalDate());
    }
}
