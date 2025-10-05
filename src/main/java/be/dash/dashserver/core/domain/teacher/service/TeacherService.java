package be.dash.dashserver.core.domain.teacher.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import be.dash.dashserver.api.core.member.MyLessonDetailedResponse;
import be.dash.dashserver.api.core.member.dto.ApplyStatus;
import be.dash.dashserver.api.core.member.dto.MyLessonResponse;
import be.dash.dashserver.api.core.member.dto.MyLessonsResponse;
import be.dash.dashserver.api.core.member.dto.MyLessonsThumbnailResponse;
import be.dash.dashserver.api.core.member.dto.MyLessonsThumbnailResponse.MyLessonThumbnailResponse;
import be.dash.dashserver.api.core.teacher.dto.ChangeApproveStatusResponse;
import be.dash.dashserver.api.core.teacher.dto.LessonStatusCountResponses;
import be.dash.dashserver.core.auth.JwtTokenGenerator;
import be.dash.dashserver.core.auth.Token;
import be.dash.dashserver.core.domain.account.Account;
import be.dash.dashserver.core.domain.account.service.AccountRepository;
import be.dash.dashserver.core.domain.common.Genre;
import be.dash.dashserver.core.domain.common.Keyword;
import be.dash.dashserver.core.domain.lesson.Lesson;
import be.dash.dashserver.core.domain.lesson.Lessons;
import be.dash.dashserver.core.domain.lesson.service.LessonRepository;
import be.dash.dashserver.core.domain.member.Member;
import be.dash.dashserver.core.domain.member.Role;
import be.dash.dashserver.core.domain.member.service.MemberRepository;
import be.dash.dashserver.core.domain.reservation.Reservation;
import be.dash.dashserver.core.domain.reservation.ReservationStatus;
import be.dash.dashserver.core.domain.reservation.ReservationStatusType;
import be.dash.dashserver.core.domain.reservation.Reservations;
import be.dash.dashserver.core.domain.reservation.service.ReservationRepository;
import be.dash.dashserver.core.domain.teacher.Teacher;
import be.dash.dashserver.core.domain.teacher.TeacherLessonGenres;
import be.dash.dashserver.core.domain.teacher.Teachers;
import be.dash.dashserver.core.domain.teacher.command.CreateTeacherCommand;
import be.dash.dashserver.core.domain.teacher.command.TeacherUpdateCommand;
import be.dash.dashserver.core.domain.teacher.service.dto.MyTeacherProfileDetailResult;
import be.dash.dashserver.core.domain.teacher.service.dto.MyTeacherProfileResult;
import be.dash.dashserver.core.domain.teacher.service.dto.TeacherDetailResult;
import be.dash.dashserver.core.exception.ConflictException;
import be.dash.dashserver.core.exception.DashException;
import be.dash.dashserver.core.exception.ForbiddenException;
import be.dash.dashserver.core.exception.NotFoundException;
import be.dash.dashserver.core.external.MessageSender;
import be.dash.dashserver.core.log.annotation.Trace;
import lombok.RequiredArgsConstructor;

@Trace
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeacherService {
    private final TeacherRepository teacherRepository;
    private final LessonRepository lessonRepository;
    private final MemberRepository memberRepository;
    private final TeacherImageRepository teacherImageRepository;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final TeacherVideoRepository teacherVideoRepository;
    private final ReservationRepository reservationRepository;
    private final AccountRepository accountRepository;
    private final MessageSender messageSender;

    public List<TeacherLessonGenres> search(Keyword keyword) {
        Teachers teachers = teacherRepository.findTeachersSortByLessonCountsDesc(keyword.getValue());
        return getTeacherLessonGenres(teachers);
    }

    private List<TeacherLessonGenres> getTeacherLessonGenres(Teachers teachers) {
        List<TeacherLessonGenres> teacherGenres = new ArrayList<>();
        teachers.teachers().forEach(teacher -> {
            List<Genre> genres = lessonRepository.findDistinctGenresByTeacherIdOrderByCountDesc(teacher.getId());
            TeacherLessonGenres teacherLessonGenres = new TeacherLessonGenres(teacher, genres);
            teacherGenres.add(teacherLessonGenres);
        });
        return teacherGenres;
    }

    @Transactional
    public Token create(CreateTeacherCommand command) {
        validateInstagram(command.instagram());
        validateYoutube(command.youtube());

        Member member = memberRepository.findById(command.memberId());
        Teacher teacher = command.toDomain(member);
        teacherRepository.register(teacher);
        memberRepository.updateRole(member.getId(), Role.TEACHER);

        return new Token(jwtTokenGenerator.createAccessToken(String.valueOf(member.getId()), Role.TEACHER),
                jwtTokenGenerator.createRefreshToken(String.valueOf(member.getId()), Role.TEACHER));
    }

    public TeacherDetailResult find(long teacherId) {
        Teacher teacher = teacherRepository.findByTeacherId(teacherId);
        List<Genre> genres = lessonRepository.findDistinctGenresByTeacherIdOrderByCountDesc(teacher.getId());
        Lessons activeLessonsByTeacher = lessonRepository.findLessonsByTeacher(teacher);
        return new TeacherDetailResult(new TeacherLessonGenres(teacher, genres), teacher.getNickname(), activeLessonsByTeacher);
    }

    public MyTeacherProfileResult findMyTeacherProfile(long memberId) {
        Teacher teacher = findTeacherByMemberId(memberId);
        Account account = accountRepository.findByMemberIdAndIsTeacherAccount(memberId);
        String image = findTeacherImageByTeacherId(teacher.getId());
        return MyTeacherProfileResult.of(image, teacher, account);
    }

    public MyTeacherProfileDetailResult findMyTeacherProfileDetail(long memberId) {
        Teacher teacher = findTeacherByMemberId(memberId);
        String image = findTeacherImageByTeacherId(teacher.getId());
        List<String> videos = teacherVideoRepository.findAllByTeacherId(teacher.getId());

        return MyTeacherProfileDetailResult.of(teacher, image, videos);
    }

    private String findTeacherImageByTeacherId(long teacherId) {
        return teacherImageRepository.findTop1ImageUrlByTeacherId(teacherId)
                .orElseThrow(() -> new NotFoundException("선생님 프로필 이미지가 존재하지 않습니다."));
    }

    @Transactional
    public void updateTeacherProfile(TeacherUpdateCommand command) {
        validateInstagramOnUpdate(command.instagram(), command.memberId());
        validateYoutubeOnUpdate(command.youtube(), command.memberId());

        Teacher teacher = teacherRepository.update(command.toTeacher(), command.memberId())
                .orElseThrow(() -> new NotFoundException("선생님 프로필이 존재하지 않습니다."));
        teacherImageRepository.replace(teacher.getId(), command.imageUrls());
        teacherVideoRepository.replace(teacher.getId(), command.videoUrls());
    }

    private void validateYoutubeOnUpdate(String youtube, long memberId) {
        if (Objects.nonNull(youtube) && teacherRepository.existByYoutubeAndMemberIdNot(youtube, memberId)) {
            throw new ConflictException("이미 사용 중인 유튜브입니다.");
        }
    }

    private void validateInstagramOnUpdate(String instagram, long memberId) {
        if (Objects.nonNull(instagram) && teacherRepository.existByInstagramAndMemberIdNot(instagram, memberId)) {
            throw new ConflictException("이미 사용 중인 인스타그램입니다.");
        }
    }

    private void validateInstagram(String instagram) {
        if (Objects.nonNull(instagram) && teacherRepository.existByInstagram(instagram)) {
            throw new ConflictException("이미 사용 중인 인스타그램입니다.");
        }
    }

    private void validateYoutube(String youtube) {
        if (Objects.nonNull(youtube) && teacherRepository.existByYoutube(youtube)) {
            throw new ConflictException("이미 사용 중인 유튜브입니다.");
        }
    }

    public MyLessonDetailedResponse getMyLesson(long memberId, long lessonId, ReservationStatusType status) {
        Teacher teacher = findTeacherByMemberId(memberId);
        Lesson lesson = lessonRepository.findLessonsById(lessonId);
        validateOwner(teacher, lesson);
        Reservations reservations = reservationRepository.findAllByLessonIdAndReservationStatusOrderByCreatedAtDesc(lessonId, status.getReservationStatuses());
        List<Member> members = memberRepository.findAllByMemberIds(reservations.getMemberIds());
        return MyLessonDetailedResponse.from(lesson, members, reservations);
    }

    private Teacher findTeacherByMemberId(long memberId) {
        return teacherRepository.findByMemberId(memberId)
                .orElseThrow(() -> new NotFoundException("해당하는 선생님을 찾을 수 없습니다."));
    }

    private void validateOwner(Teacher teacher, Lesson lesson) {
        if (!lessonRepository.existsByTeacherIdAndLessonId(teacher.getId(), lesson.getId())) {
            throw new ForbiddenException("해당하는 수업에 대한 권한이 없습니다.");
        }
    }

    public MyLessonsResponse getMyLessons(long memberId, ApplyStatus status) {
        if (Objects.isNull(status)) {
            List<Lesson> lessons = getLessons(memberId);
            return MyLessonsResponse.from(lessons.stream().map(MyLessonResponse::from).toList());
        }
        List<Lesson> lessons = getLessons(memberId);
        List<MyLessonResponse> myLessonResponses = lessons.stream().map(MyLessonResponse::from)
                .filter(myLessonResponse -> myLessonResponse.applyStatus() == status).toList();
        return MyLessonsResponse.from(myLessonResponses);
    }

    public MyLessonsThumbnailResponse getMyLessonsThumbnail(long memberId) {
        List<Lesson> lessons = getLessons(memberId);
        return MyLessonsThumbnailResponse.from(lessons.stream().map(MyLessonThumbnailResponse::from).toList());
    }

    private List<Lesson> getLessons(long memberId) {
        Teacher teacher = findTeacherByMemberId(memberId);
        return lessonRepository.findAllByTeacherIdOOrderByCreatedAtDesc(teacher.getId());
    }

    public LessonStatusCountResponses getMyLessonsStatusCount(Long memberId) {
        List<Lesson> lessons = getLessons(memberId);
        return LessonStatusCountResponses.from(lessons);

    }

    public boolean checkNicknameDuplication(String nickname) {
        return teacherRepository.existsByNickname(nickname);
    }

    @Transactional
    public ChangeApproveStatusResponse changeApproveStatus(Long memberId, Long lessonId, Long reservationId) {
        Teacher teacher = findTeacherByMemberId(memberId);
        Lesson lesson = lessonRepository.findLessonsById(lessonId);
        validateOwner(teacher, lesson);
        Reservation reservation = reservationRepository.findById(reservationId);
        if (reservation.getReservationStatus() == ReservationStatus.APPROVED) {
            return changeStatusToPendingApprove(lessonId, reservationId);
        } else if (reservation.getReservationStatus() == ReservationStatus.PENDING_APPROVAL && !lessonRepository.isFull(lessonId)) {
            return changeStatusToApprove(teacher, lesson, reservation);
        } else if (reservation.getReservationStatus() == ReservationStatus.PENDING_APPROVAL && lessonRepository.isFull(lessonId)) {
            return new ChangeApproveStatusResponse(true);
        }
        throw new DashException("승인과 관련된 예약 상태를 변경할 수 없습니다.");
    }

    private ChangeApproveStatusResponse changeStatusToApprove(Teacher teacher, Lesson lesson, Reservation reservation) {
        reservationRepository.approve(reservation.getId());
        lessonRepository.increaseReservationCount(lesson.getId());
        Member member = memberRepository.findById(reservation.getMemberId());
        sendClassConfirmed(teacher, lesson, member);

        return new ChangeApproveStatusResponse(false);
    }

    private void sendClassConfirmed(Teacher teacher, Lesson lesson, Member member) {
        String to = member.getPhoneNumber();
        String studentName = member.getName();
        String instructorName = teacher.getNickname();
        String className = lesson.getName();
        messageSender.sendClassConfirmed(to, studentName, instructorName, className);
    }

    private ChangeApproveStatusResponse changeStatusToPendingApprove(Long lessonId, Long reservationId) {
        reservationRepository.pendingApprove(reservationId);
        lessonRepository.decreaseReservationCount(lessonId);
        return new ChangeApproveStatusResponse(false);
    }

    @Transactional
    public void changeCancelStatus(Long memberId, Long lessonId, Long reservationId) {
        Teacher teacher = findTeacherByMemberId(memberId);
        Lesson lesson = lessonRepository.findLessonsById(lessonId);
        validateOwner(teacher, lesson);
        Reservation reservation = reservationRepository.findById(reservationId);
        if (reservation.getReservationStatus() == ReservationStatus.CANCELLED) {
            reservationRepository.pendingCancel(reservationId);
            return;
        } else if (reservation.getReservationStatus() == ReservationStatus.PENDING_CANCELLATION) {
            reservationRepository.cancel(reservationId);
            Member member = memberRepository.findById(reservation.getMemberId());
            sendCancelDone(member, teacher, lesson);
            return;
        }
        throw new DashException("취소와 관련된 예약 상태를 변경할 수 없습니다.");
    }

    private void sendCancelDone(Member member, Teacher teacher, Lesson lesson) {
        String to = member.getPhoneNumber();          // 수강생 번호
        String studentName = member.getName();        // 수강생 이름
        String instructorName = teacher.getNickname();  // 강사 이름
        String className = lesson.getName();          // 클래스 이름

        messageSender.sendCancelDone(
                to,
                studentName,
                instructorName,
                className
        );
    }
}
