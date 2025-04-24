package be.dash.dashserver.api.core.member.dto;

import java.time.LocalDateTime;
import java.util.List;
import be.dash.dashserver.core.domain.common.Genre;
import be.dash.dashserver.core.domain.common.Level;
import be.dash.dashserver.core.domain.lesson.Lesson;

public record MyLessonsThumbnailResponse(List<MyLessonThumbnailResponse> lessons) {
    private static final int D_DAY_ADJUSTER = -1;


    public static MyLessonsThumbnailResponse from(List<MyLessonThumbnailResponse> lessons) {
        return new MyLessonsThumbnailResponse(lessons);
    }

    public record MyLessonThumbnailResponse(long id,
                                            String name,
                                            String imageUrl,
                                            Genre genre,
                                            Level level,
                                            int dDay) {
        public static MyLessonThumbnailResponse from(Lesson lesson) {
            return new MyLessonThumbnailResponse(
                    lesson.getId(),
                    lesson.getName(),
                    lesson.getRepresentativeImageUrl(),
                    lesson.getGenre(),
                    lesson.getLevel(),
                    D_DAY_ADJUSTER * (int) LocalDateTime.now().until(lesson.getStartTime(), java.time.temporal.ChronoUnit.DAYS));
        }
    }
}
