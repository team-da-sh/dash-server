package be.dash.dashserver.core.domain.reservation.service;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import be.dash.dashserver.api.core.lesson.dto.LessonAccount;
import be.dash.dashserver.core.domain.account.Account;
import be.dash.dashserver.core.domain.account.service.AccountRepository;
import be.dash.dashserver.core.domain.lesson.Lesson;
import be.dash.dashserver.core.domain.lesson.service.LessonRepository;
import be.dash.dashserver.core.domain.member.Member;
import be.dash.dashserver.core.domain.member.service.MemberRepository;
import be.dash.dashserver.core.domain.payment.PaymentClientApi;
import be.dash.dashserver.core.domain.reservation.Reservation;
import be.dash.dashserver.core.domain.reservation.command.CreateReservationCommand;
import be.dash.dashserver.core.domain.teacher.Teacher;
import be.dash.dashserver.core.domain.teacher.service.TeacherRepository;
import be.dash.dashserver.core.exception.ConflictException;
import be.dash.dashserver.core.external.MessageSender;
import be.dash.dashserver.core.log.annotation.Trace;
import lombok.RequiredArgsConstructor;

@Trace
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final LessonRepository lessonRepository;
    private final TeacherRepository teacherRepository;
    private final AccountRepository accountRepository;
    private final PaymentClientApi paymentClientApi;
    private final MessageSender messageSender;

    public Reservation findById(long reservationId) {
        return reservationRepository.findById(reservationId);
    }

    public boolean isBooked(long memberId, long lessonId) {
        return reservationRepository.existsApproveReservation(memberId, lessonId);
    }

    @Transactional
    public long reservePayment(CreateReservationCommand command) {
        validateReservationAvailability(command);
        Member member = memberRepository.findById(command.memberId());
        paymentClientApi.purchase(command.toPaymentInformation());
        long reservationId;
        try {
            reservationId = reservationRepository.save(member.getId(), command.lessonId());
        } catch (DataIntegrityViolationException e) {
            paymentClientApi.cancelByInternalError(command.paymentKey());
            throw new ConflictException("이미 예약된 수업입니다.");
        }
        lessonRepository.increaseReservationCount(command.lessonId());
        return reservationId;
    }

    private void validateReservationAvailability(CreateReservationCommand command) {
        validateOwnerIfTeacher(command.memberId(), command.lessonId());
        if (lessonRepository.isFull(command.lessonId())) {
            throw new ConflictException("잔여 좌석이 없습니다.");
        }
    }

    @Transactional
    public LessonAccount reserve(long memberId, long lessonId) {
        validateReservationAvailability(memberId, lessonId);
        Member member = memberRepository.findById(memberId);
        reservationRepository.save(member.getId(), lessonId);
        Lesson lesson = lessonRepository.findLessonsById(lessonId);
        Teacher teacher = lesson.getTeacher();
        Account teacherAccount = accountRepository.findByLessonId(lessonId);
        sendClassApply(teacher, lesson, member);
        return new LessonAccount(lesson, teacherAccount);
    }

    private void sendClassApply(Teacher teacher, Lesson lesson, Member member) {
        String instructorPhone = teacher.getMember().getPhoneNumber();
        String instructorName = teacher.getNickname();
        String className = lesson.getName();
        String studentName = member.getName();
        String studentPhone = member.getPhoneNumber();

        messageSender.sendClassApply(
                instructorPhone,
                instructorName,
                className,
                studentName,
                studentPhone
        );
    }

    private void validateReservationAvailability(long memberId, long lessonId) {
        validateOwnerIfTeacher(memberId, lessonId);
        if (lessonRepository.isFull(lessonId)) {
            throw new ConflictException("잔여 좌석이 없습니다.");
        }
    }

    private void validateOwnerIfTeacher(long memberId, long lessonId) {
        teacherRepository.findByMemberId(memberId)
                .ifPresent(teacher -> {
                    validateOwner(teacher.getId(), lessonId);
                });
    }

    private void validateOwner(long teacherId, long lessonId) {
        if (lessonRepository.existsByTeacherIdAndLessonId(teacherId, lessonId)) {
            throw new ConflictException("본인 수업은 수강할 수 없습니다.");
        }
    }
}
