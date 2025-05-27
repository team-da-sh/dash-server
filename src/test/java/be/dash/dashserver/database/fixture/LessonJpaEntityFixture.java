package be.dash.dashserver.database.fixture;

import java.time.LocalDateTime;
import be.dash.dashserver.core.domain.common.Genre;
import be.dash.dashserver.core.domain.common.Level;
import be.dash.dashserver.database.core.lesson.LessonJpaEntity;
import be.dash.dashserver.database.core.teacher.TeacherJpaEntity;

public class LessonJpaEntityFixture {
    private LessonJpaEntityFixture() {
    }

    public static LessonJpaEntity create(TeacherJpaEntity teacherJpaEntity, LocalDateTime startTime, LocalDateTime endTime) {
        return LessonJpaEntity.builder()
                .teacher(teacherJpaEntity)
                .name("박재연의 미친 웨이브")
                .genre(Genre.HIPHOP)
                .level(Level.BEGINNER)
                .location("서울 광진구")
                .favoriteCount(100L)
                .reservationCount(50L)
                .maxReservationCount(100L)
                .startDateTime(startTime)
                .endDateTime(endTime)
                .detail("수업에 대한 상세 설명")
                .recommendation("수강 추천사")
                .price(50000)
                .build();
    }
}
