package be.dash.dashserver.core.domain.member.service;

import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import be.dash.dashserver.api.core.member.dto.ReservationCancelRequest;
import be.dash.dashserver.api.core.member.dto.ReservationDetailedResponse;
import be.dash.dashserver.api.core.member.dto.ReservationStatisticsResponse;
import be.dash.dashserver.api.core.member.dto.ReservationStatusCountResponses;
import be.dash.dashserver.core.domain.lesson.Lesson;
import be.dash.dashserver.core.domain.lesson.service.LessonRepository;
import be.dash.dashserver.core.domain.member.Member;
import be.dash.dashserver.core.domain.member.command.MemberUpdateCommand;
import be.dash.dashserver.core.domain.member.command.OnboardCommand;
import be.dash.dashserver.core.domain.reservation.Reservation;
import be.dash.dashserver.core.domain.reservation.ReservationStatus;
import be.dash.dashserver.core.domain.reservation.Reservations;
import be.dash.dashserver.core.domain.reservation.service.ReservationRepository;
import be.dash.dashserver.core.exception.ConflictException;
import be.dash.dashserver.core.exception.ForbiddenException;
import be.dash.dashserver.core.log.annotation.Trace;
import lombok.RequiredArgsConstructor;

import static be.dash.dashserver.core.domain.reservation.ReservationStatus.CANCELLED;
import static be.dash.dashserver.core.domain.reservation.ReservationStatus.PENDING_CANCELLATION;

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
        validatePhoneNumber(command.phoneNumber());

        Member member = command.toMember();
        memberRepository.onboard(member);
    }

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId);
    }

    public List<ReservationResult> getMemberReservations(Long memberId, ReservationStatus status) {
        Reservations reservations = reservationRepository.findAllByMemberIdAndStatus(memberId, status);
        Set<Long> lessonIds = reservations.getLessonIds();
        List<Lesson> myLessons = lessonRepository.findAllByIdsOrderByStartDate(lessonIds);
        return myLessons.stream()
                .map(lesson -> ReservationResult.of(lesson, reservations))
                .toList();
    }

    public ReservationStatusCountResponses getMemberReservationsStatusCount(Long memberId) {
        Reservations reservations = reservationRepository.findAllByMemberIdAndStatus(memberId, null);
        return ReservationStatusCountResponses.from(reservations);
    }

    @Transactional
    public void updateMemberInformation(MemberUpdateCommand command) {
        validatePhoneNumberOnUpdate(command.phoneNumber(), command.memberId());
        memberRepository.update(command.toMember());
    }

    private void validatePhoneNumberOnUpdate(String phoneNumber, Long memberId) {
        if (memberRepository.existsByPhoneNumberAndIdNot(phoneNumber, memberId)) {
            throw new ConflictException("이미 사용 중인 전화번호입니다.");
        }
    }

    private void validatePhoneNumber(String phoneNumber) {
        if (memberRepository.existsByPhoneNumber(phoneNumber)) {
            throw new ConflictException("이미 사용 중인 전화번호입니다.");
        }
    }

    public ReservationResult getMemberReservation(long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId);
        Lesson lesson = lessonRepository.findLessonsById(reservation.getLessonId());
        return ReservationResult.of(lesson, reservation);
    }


    public ReservationDetailedResponse getMemberReservationDetailed(long memberId, long reservationId) {
        Member member = memberRepository.findById(memberId);
        Reservation reservation = reservationRepository.findById(reservationId);
        Lesson lesson = lessonRepository.findLessonsById(reservation.getLessonId());
        return ReservationDetailedResponse.from(member, reservation, lesson);
    }

    @Transactional
    public void cancelMemberReservation(long memberId, long reservationId, ReservationCancelRequest request) {
        Reservation reservation = reservationRepository.findById(reservationId);
        if (!reservation.ownBy(memberId)) {
            throw new ForbiddenException("예약을 취소할 권한이 없습니다.");
        }
        if (request.toCancelReservationCommand().reservationStatus() == CANCELLED) {
            reservationRepository.cancel(reservationId);
            return;
        }
        if (request.toCancelReservationCommand().reservationStatus() == PENDING_CANCELLATION) {
            reservationRepository.pendingCancel(reservationId);
            // 문자 여기서 보내야함.
        }
    }

    public ReservationStatisticsResponse getReservationStatistics(Long memberId) {
        return new ReservationStatisticsResponse(
                reservationRepository.countUpcomingReservationsByMemberId(memberId),
                reservationRepository.countOngoingReservationsByMemberId(memberId),
                reservationRepository.countPastReservationsByMemberId(memberId)
        );
    }
}
