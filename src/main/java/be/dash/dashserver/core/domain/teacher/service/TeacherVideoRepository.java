package be.dash.dashserver.core.domain.teacher.service;

import java.util.List;

public interface TeacherVideoRepository {
    List<String> findAllByTeacherId(long teacherId);
    void replace(long teacherId, List<String> strings);
}
