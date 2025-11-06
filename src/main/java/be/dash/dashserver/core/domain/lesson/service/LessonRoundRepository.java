package be.dash.dashserver.core.domain.lesson.service;

import java.util.List;
import be.dash.dashserver.core.domain.lesson.Round;

public interface LessonRoundRepository {
    void replace(long lessonId, List<Round> rounds);
}

