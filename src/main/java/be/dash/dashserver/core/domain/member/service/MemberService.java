package be.dash.dashserver.core.domain.member.service;

import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import be.dash.dashserver.api.core.member.dto.ReservationCancelRequest;
import be.dash.dashserver.api.core.member.dto.ReservationDetailedResponse;
import be.dash.dashserver.api.core.member.dto.ReservationStatisticsResponse;
import be.dash.dashserver.core.domain.lesson.Lesson;
import be.dash.dashserver.core.domain.lesson.service.LessonRepository;
import be.dash.dashserver.core.domain.member.Member;
import be.dash.dashserver.core.domain.member.command.MemberUpdateCommand;
import be.dash.dashserver.core.domain.member.command.OnboardCommand;
import be.dash.dashserver.core.domain.reservation.Reservation;
import be.dash.dashserver.core.domain.reservation.Reservations;
import be.dash.dashserver.core.domain.reservation.service.ReservationRepository;
import be.dash.dashserver.core.exception.ConflictException;
import be.dash.dashserver.core.log.annotation.Trace;
import lombok.RequiredArgsConstructor;

@Trace
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final LessonRepository lessonRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public void onboard(OnboardCommand command) {
        validateNickname(command.nickname());
        validatePhoneNumber(command.phoneNumber());

        Member member = command.toMember();
        memberRepository.onboard(member);
    }

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId);
    }

    public List<ReservationResult> getMemberReservations(Long memberId) {
        Reservations reservations = reservationRepository.findAllByMemberId(memberId);
        Set<Long> lessonIds = reservations.getLessonIds();
        List<Lesson> myLessons = lessonRepository.findAllByIdsOrderByStartDate(lessonIds);
        return myLessons.stream()
                .map(lesson -> ReservationResult.of(lesson, reservations))
                .toList();
    }

    @Transactional
    public void updateMemberInformation(MemberUpdateCommand command) {
        validateNicknameOnUpdate(command.nickname(), command.memberId());
        validatePhoneNumberOnUpdate(command.phoneNumber(), command.memberId());

        memberRepository.update(command.toMember());
    }

    private void validateNicknameOnUpdate(String nickname, Long memberId) {
        if (memberRepository.existsByNicknameAndIdNot(nickname, memberId)) {
            throw new ConflictException("이미 사용 중인 닉네임입니다.");
        }
    }

    private void validatePhoneNumberOnUpdate(String phoneNumber, Long memberId) {
        if (memberRepository.existsByPhoneNumberAndIdNot(phoneNumber, memberId)) {
            throw new ConflictException("이미 사용 중인 전화번호입니다.");
        }
    }

    private void validateNickname(String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw new ConflictException("이미 사용 중인 닉네임입니다.");
        }
    }

    private void validatePhoneNumber(String phoneNumber) {
        if (memberRepository.existsByPhoneNumber(phoneNumber)) {
            throw new ConflictException("이미 사용 중인 전화번호입니다.");
        }
    }

    public ReservationDetailedResponse getMemberReservation(long memberId, long reservationId) {
        Member member = memberRepository.findById(memberId);
        Reservation reservation = reservationRepository.findById(reservationId);
        Lesson lesson = lessonRepository.findLessonsById(reservation.getLessonId());
        return ReservationDetailedResponse.from(member, reservation, lesson);
    }

    @Transactional
    public void cancelMemberReservation(long memberId, long reservationId, ReservationCancelRequest request) {
        reservationRepository.cancel(memberId, reservationId, request.toCancelReservationCommand());
    }

    public ReservationStatisticsResponse getReservationStatistics(Long memberId) {
        return new ReservationStatisticsResponse(
                reservationRepository.countUpcomingReservationsByMemberId(memberId),
                reservationRepository.countOngoingReservationsByMemberId(memberId),
                reservationRepository.countPastReservationsByMemberId(memberId)
        );
    }
}
