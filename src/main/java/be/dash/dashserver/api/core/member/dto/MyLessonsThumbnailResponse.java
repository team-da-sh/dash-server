package be.dash.dashserver.api.core.member.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import be.dash.dashserver.core.domain.common.Genre;
import be.dash.dashserver.core.domain.common.Level;
import be.dash.dashserver.core.domain.lesson.Lesson;

public record MyLessonsThumbnailResponse(List<MyLessonThumbnailResponse> lessons) {
    public static MyLessonsThumbnailResponse from(List<MyLessonThumbnailResponse> lessons) {
        return new MyLessonsThumbnailResponse(lessons);
    }

    public record MyLessonThumbnailResponse(long id,
                                            String name,
                                            String imageUrl,
                                            Genre genre,
                                            Level level,
                                            long dDay) {
        public static MyLessonThumbnailResponse from(Lesson lesson) {
            return new MyLessonThumbnailResponse(
                    lesson.getId(),
                    lesson.getName(),
                    lesson.getRepresentativeImageUrl(),
                    lesson.getGenre(),
                    lesson.getLevel(),
                    ChronoUnit.DAYS.between(LocalDate.now(), lesson.getStartTime())
            );
        }
    }
}
