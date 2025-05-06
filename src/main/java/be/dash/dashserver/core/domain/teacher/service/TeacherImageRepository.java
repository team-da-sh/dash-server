package be.dash.dashserver.core.domain.teacher.service;

import java.util.List;
import java.util.Optional;
import be.dash.dashserver.core.domain.teacher.Teacher;

public interface TeacherImageRepository {
    void saveAll(Teacher teacher);
    Optional<String> findTop1ImageUrlByTeacherId(long teacherId);
    void replace(Long id, List<String> imageUrls);
}
