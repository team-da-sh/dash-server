package be.dash.dashserver.database.core.lesson;

import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import be.dash.dashserver.core.domain.lesson.Round;
import be.dash.dashserver.core.domain.lesson.service.LessonRoundRepository;
import be.dash.dashserver.database.core.teacher.TeacherImageJpaEntity;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LessonRoundRepositoryAdapter implements LessonRoundRepository {
    private final LessonRoundJpaRepository lessonRoundJpaRepository;
    @Override
    public void replace(long lessonId, List<Round> rounds) {
        lessonRoundJpaRepository.deleteAllByLessonId(lessonId);
        lessonRoundJpaRepository.saveAll(rounds.stream()
                .map(round -> new LessonRoundJpaEntity(lessonId, round.getStartTime(), round.getEndTime())).toList());
    }
}
