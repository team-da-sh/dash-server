package be.dash.dashserver.core.auth;

import java.util.List;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import be.dash.dashserver.core.domain.lesson.Lesson;
import be.dash.dashserver.core.domain.lesson.service.LessonRepository;
import be.dash.dashserver.core.domain.member.Member;
import be.dash.dashserver.core.domain.member.Role;
import be.dash.dashserver.core.domain.member.service.MemberRepository;
import be.dash.dashserver.core.domain.reservation.Reservations;
import be.dash.dashserver.core.domain.reservation.service.ReservationRepository;
import be.dash.dashserver.core.domain.teacher.Teacher;
import be.dash.dashserver.core.domain.teacher.service.TeacherRepository;
import be.dash.dashserver.core.exception.BadRequestException;
import be.dash.dashserver.core.exception.ConflictException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WithdrawService {

    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final LessonRepository lessonRepository;
    private final TeacherRepository teacherRepository;

    @Transactional
    public void withdraw(Long memberId, Role role) {
        validateConditions(memberId, role);

        memberRepository.withdraw(memberId);
    }

    public void validateWithdrawal(Long memberId, Role role) {
        validateConditions(memberId, role);
    }

    private void validateConditions(Long memberId, Role role) {
        Reservations reservations = reservationRepository.findAllByMemberId(memberId);
        reservations.validateMemberWithdrawal();

        if (role == Role.TEACHER) {
            Teacher teacher = teacherRepository.findByMemberId(memberId).get();
            List<Lesson> list = lessonRepository.findAllByTeacherIdOOrderByCreatedAtDesc(teacher.getId())
                    .stream().toList();

            boolean hasOngoingLesson = list.stream().anyMatch(Lesson::isOngoing);
            if (hasOngoingLesson) {
                throw new ConflictException("진행중인 수업이 있어 탈퇴할 수 없습니다.");
            }

            List<Long> teacherLessonIds = list.stream().map(Lesson::getId).toList();
            Reservations teacherCustomerReservations = reservationRepository.findAllByLessonIds(teacherLessonIds);
            teacherCustomerReservations.validateTeacherWithdrawal();
        }
    }
}
