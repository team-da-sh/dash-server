package be.dash.dashserver.database.core.teacher;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TeacherImageJpaRepository extends JpaRepository<TeacherImageJpaEntity, Long> {
    List<TeacherImageJpaEntity> findAllByTeacherId(long teacherId);
    Optional<TeacherImageJpaEntity> findTop1ByTeacherId(long teacherId);
    @Modifying
    @Query("DELETE FROM TeacherImageJpaEntity ti WHERE ti.teacherId = :id AND ti.imageUrl NOT IN :strings")
    void deleteByTeacherIdAndImageUrlNotIn(Long id, List<String> strings);
}
