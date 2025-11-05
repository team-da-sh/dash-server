package be.dash.dashserver.core.domain.lesson.service;

import java.util.List;
import be.dash.dashserver.database.core.teacher.TeacherImageJpaEntity;

public interface LessonImageRepository {
    void replace(long lessonId, List<String> imageUrls);
}
