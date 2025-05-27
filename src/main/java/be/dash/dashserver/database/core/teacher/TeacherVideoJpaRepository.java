package be.dash.dashserver.database.core.teacher;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TeacherVideoJpaRepository extends JpaRepository<TeacherVideoJpaEntity, Long> {
    List<TeacherVideoJpaEntity> findAllByTeacherId(Long teacherId);

    void deleteByTeacherId(long teacherId);
}
