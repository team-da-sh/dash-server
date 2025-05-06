package be.dash.dashserver.database.core.teacher;

import java.util.List;
import org.springframework.stereotype.Repository;
import be.dash.dashserver.core.domain.teacher.service.TeacherVideoRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TeacherVideoRepositoryAdapter implements TeacherVideoRepository {

    private final TeacherVideoJpaRepository teacherVideoJpaRepository;
    @Override
    public List<String> findAllByTeacherId(long teacherId) {
        return teacherVideoJpaRepository.findAllByTeacherId(teacherId)
                .stream()
                .map(TeacherVideoJpaEntity::getVideoUrl)
                .toList();
    }

    @Override
    public void replace(long teacherId, List<String> videoUrls) {
        teacherVideoJpaRepository.deleteByTeacherIdAndVideoUrlNotIn(teacherId, videoUrls);
        teacherVideoJpaRepository.saveAll(
                videoUrls.stream().map(videoUrl -> new TeacherVideoJpaEntity(teacherId, videoUrl)).toList()
        );
    }
}
