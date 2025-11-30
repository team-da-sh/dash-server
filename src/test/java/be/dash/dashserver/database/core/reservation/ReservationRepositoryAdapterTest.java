package be.dash.dashserver.database.core.reservation;

import java.time.LocalDateTime;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import com.fasterxml.jackson.databind.ObjectMapper;
import be.dash.dashserver.core.domain.reservation.service.ReservationRepository;
import be.dash.dashserver.database.core.lesson.LessonJpaEntity;
import be.dash.dashserver.database.core.lesson.LessonJpaRepository;
import be.dash.dashserver.database.core.member.MemberJpaEntity;
import be.dash.dashserver.database.core.member.MemberJpaRepository;
import be.dash.dashserver.database.core.teacher.TeacherJpaEntity;
import be.dash.dashserver.database.core.teacher.TeacherJpaRepository;
import be.dash.dashserver.database.fixture.LessonJpaEntityFixture;
import be.dash.dashserver.database.fixture.MemberJpaEntityFixture;
import be.dash.dashserver.database.fixture.TeacherJpaEntityFixture;

@DataJpaTest
@Import({
        ReservationRepositoryAdapter.class,
        ObjectMapper.class
})
class ReservationRepositoryAdapterTest {
    @Autowired
    private MemberJpaRepository memberJpaRepository;
    @Autowired
    private TeacherJpaRepository teacherJpaRepository;
    @Autowired
    private LessonJpaRepository lessonJpaRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Transactional
    @DisplayName("수강전인 수업의 수를 반환한다.")
    @Test
    void countUpcomingReservationsByMemberId() {
        // given
        MemberJpaEntity memberJpaEntity = MemberJpaEntityFixture.create();
        memberJpaRepository.save(memberJpaEntity);
        TeacherJpaEntity teacherJpaEntity = TeacherJpaEntityFixture.create(memberJpaEntity);
        teacherJpaRepository.save(teacherJpaEntity);
        LessonJpaEntity lessonJpaEntity = LessonJpaEntityFixture.create(teacherJpaEntity,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        lessonJpaRepository.save(lessonJpaEntity);
        long reservationId = reservationRepository.save(memberJpaEntity.getId(), lessonJpaEntity.getId());
        reservationRepository.approve(reservationId);

        // when
        int count = reservationRepository.countUpcomingReservationsByMemberId(memberJpaEntity.getId());
        // then
        Assertions.assertThat(count).isEqualTo(1);
    }
    @Transactional
    @DisplayName("수강중인 수업의 수를 반환한다.")
    @Test
    void countOngoingReservationsByMemberId() {
        // given
        MemberJpaEntity memberJpaEntity = MemberJpaEntityFixture.create();
        memberJpaRepository.save(memberJpaEntity);
        TeacherJpaEntity teacherJpaEntity = TeacherJpaEntityFixture.create(memberJpaEntity);
        teacherJpaRepository.save(teacherJpaEntity);
        LessonJpaEntity lessonJpaEntity = LessonJpaEntityFixture.create(teacherJpaEntity,
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(2));
        lessonJpaRepository.save(lessonJpaEntity);
        long reservationId = reservationRepository.save(memberJpaEntity.getId(), lessonJpaEntity.getId());
        reservationRepository.inProgress(reservationId);
        // when
        int count = reservationRepository.countOngoingReservationsByMemberId(memberJpaEntity.getId());
        // then
        Assertions.assertThat(count).isEqualTo(1);
    }

    @DisplayName("수강이 끝난 수업의 수를 반환한다.")
    @Test
    void countPastReservationsByMemberId() {
        // given
        MemberJpaEntity memberJpaEntity = MemberJpaEntityFixture.create();
        memberJpaRepository.save(memberJpaEntity);
        TeacherJpaEntity teacherJpaEntity = TeacherJpaEntityFixture.create(memberJpaEntity);
        teacherJpaRepository.save(teacherJpaEntity);
        LessonJpaEntity lessonJpaEntity = LessonJpaEntityFixture.create(teacherJpaEntity,
                LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
        lessonJpaRepository.save(lessonJpaEntity);
        long reservationId = reservationRepository.save(memberJpaEntity.getId(), lessonJpaEntity.getId());
        reservationRepository.completed(reservationId);
        // when
        int count = reservationRepository.countPastReservationsByMemberId(memberJpaEntity.getId());
        // then
        Assertions.assertThat(count).isEqualTo(1);
    }
}
