package be.dash.dashserver.database.core.teacher;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TeacherImageJpaRepository extends JpaRepository<TeacherImageJpaEntity, Long> {
    List<TeacherImageJpaEntity> findAllByTeacherId(long teacherId);

    @Query("""
    select ti
    from TeacherImageJpaEntity ti
    join TeacherJpaEntity t on ti.teacherId = t.id
    join t.member m
    where ti.teacherId = :teacherId
      and m.isDeleted = false
    order by ti.id desc
    """)
    Optional<TeacherImageJpaEntity> findTop1ByTeacherId(long teacherId);

    void deleteByTeacherId(long teacherId);
}
