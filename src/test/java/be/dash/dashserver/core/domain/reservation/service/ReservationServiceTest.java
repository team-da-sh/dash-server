package be.dash.dashserver.core.domain.reservation.service;

import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import be.dash.dashserver.ServiceSliceTest;
import be.dash.dashserver.core.domain.common.Genre;
import be.dash.dashserver.core.domain.common.Level;
import be.dash.dashserver.core.domain.lesson.Lesson;
import be.dash.dashserver.core.domain.lesson.service.LessonRepository;
import be.dash.dashserver.core.domain.member.Member;
import be.dash.dashserver.core.domain.member.service.MemberRepository;
import be.dash.dashserver.core.domain.teacher.Teacher;
import be.dash.dashserver.core.domain.teacher.service.TeacherRepository;
import be.dash.dashserver.core.fixture.LessonFixture;
import be.dash.dashserver.core.fixture.MemberFixture;
import be.dash.dashserver.core.fixture.TeacherFixture;

class ReservationServiceTest extends ServiceSliceTest {
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    @DisplayName("수강전인 수업의 수를 반환한다.")
    @Test
    void countUpcomingReservationsByMemberId() {
        // given
        Member member = MemberFixture.createMember();
        memberRepository.save(member);
        Teacher teacher = TeacherFixture.createWithoutId(1);
        teacherRepository.save(teacher);

        Lesson lesson = LessonFixture.create(1, 1, Genre.BRAKING, Level.BEGINNER, LocalDateTime.now()
                .plusDays(1), LocalDateTime.now().plusDays(2));

        lessonRepository.save(lesson);
        reservationRepository.save(1, 1);
        // when
        int count = reservationService.countUpcomingReservationsByMemberId(1L);
        // then
        Assertions.assertThat(count).isEqualTo(1);
    }

    @DisplayName("수강중인 수업의 수를 반환한다.")
    @Test
    void countOngoingReservationsByMemberId() {
        // given
        Member member = MemberFixture.createMember();
        memberRepository.save(member);
        Teacher teacher = TeacherFixture.createWithoutId(1);
        teacherRepository.save(teacher);

        Lesson lesson = LessonFixture.create(1, 1, Genre.BRAKING, Level.BEGINNER, LocalDateTime.now()
                .minusDays(1), LocalDateTime.now().plusDays(2));

        lessonRepository.save(lesson);
        reservationRepository.save(1, 1);
        // when
        int count = reservationService.countOngoingReservationsByMemberId(1L);
        // then
        Assertions.assertThat(count).isEqualTo(1);
    }

    @DisplayName("수강이 끝난 수업의 수를 반환한다.")
    @Test
    void countPastReservationsByMemberId() {
        // given
        Member member = MemberFixture.createMember();
        memberRepository.save(member);
        Teacher teacher = TeacherFixture.createWithoutId(1);
        teacherRepository.save(teacher);

        Lesson lesson = LessonFixture.create(1, 1, Genre.BRAKING, Level.BEGINNER, LocalDateTime.now()
                .minusDays(2), LocalDateTime.now().minusDays(1));

        lessonRepository.save(lesson);
        reservationRepository.save(1, 1);
        // when
        int count = reservationService.countPastReservationsByMemberId(1L);
        // then
        Assertions.assertThat(count).isEqualTo(1);
    }

}
