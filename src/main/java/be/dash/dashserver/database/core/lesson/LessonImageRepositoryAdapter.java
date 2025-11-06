package be.dash.dashserver.database.core.lesson;

import java.util.List;
import org.springframework.stereotype.Repository;
import be.dash.dashserver.core.domain.lesson.service.LessonImageRepository;
import be.dash.dashserver.database.core.teacher.TeacherImageJpaEntity;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LessonImageRepositoryAdapter implements LessonImageRepository {
    private final LessonImageJpaRepository lessonImageJpaRepository;
    @Override
    public void replace(long lessonId, List<String> imageUrls) {
        lessonImageJpaRepository.deleteAllByLessonId(lessonId);
        lessonImageJpaRepository.saveAll(
                imageUrls.stream().map(imageUrl -> new LessonImageJpaEntity(lessonId, imageUrl) {
                }).toList()
        );
    }
}
