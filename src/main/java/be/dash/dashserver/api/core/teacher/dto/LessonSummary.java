package be.dash.dashserver.api.core.teacher.dto;

import java.time.Duration;
import java.time.LocalDateTime;
import be.dash.dashserver.api.core.lesson.dto.LessonRoundResponse;
import be.dash.dashserver.core.domain.lesson.Lesson;

public record LessonSummary(long id,
                            String genre,
                            String level,
                            String name,
                            String imageUrl,
                            long remainingDays) {

    public LessonSummary(Lesson lesson) {
        this(lesson.getId(), lesson.getGenre().name(), lesson.getLevel().name(), lesson.getName(), lesson.getImages()
                .getFirstImage(), calculateRemainingDays(lesson.getStartTime()));
    }

    private static long calculateRemainingDays(LocalDateTime startDateTime) {
        return LessonRoundResponse.calculateRemainingDays(startDateTime);
    }
}
