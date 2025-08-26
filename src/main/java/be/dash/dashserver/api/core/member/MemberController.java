package be.dash.dashserver.api.core.member;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import be.dash.dashserver.api.core.member.dto.MemberResponse;
import be.dash.dashserver.api.core.member.dto.MemberUpdateRequest;
import be.dash.dashserver.api.core.member.dto.OnBoardRequest;
import be.dash.dashserver.api.core.member.dto.ReservationCancelRequest;
import be.dash.dashserver.api.core.member.dto.ReservationDetailedResponse;
import be.dash.dashserver.api.core.member.dto.ReservationResponse;
import be.dash.dashserver.api.core.member.dto.ReservationStatisticsResponse;
import be.dash.dashserver.api.core.member.dto.ReservationStatusCountResponses;
import be.dash.dashserver.api.core.member.dto.ReservationsResponse;
import be.dash.dashserver.api.support.MemberId;
import be.dash.dashserver.api.support.Permission;
import be.dash.dashserver.core.domain.member.Role;
import be.dash.dashserver.core.domain.member.service.MemberService;
import be.dash.dashserver.core.domain.member.service.ReservationResult;
import be.dash.dashserver.core.domain.reservation.ReservationStatus;
import be.dash.dashserver.core.log.annotation.Trace;
import lombok.RequiredArgsConstructor;

@Trace
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    @Permission(role = Role.MEMBER)
    @PostMapping("/onboard")
    public ResponseEntity<Void> onboard(@MemberId Long memberId,
                                        @Valid @RequestBody OnBoardRequest request) {
        memberService.onboard(request.toCommand(memberId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getMemberInformation(@MemberId Long memberId) {
        return ResponseEntity.ok(MemberResponse.from(memberService.findById(memberId)));
    }

    @PatchMapping("/me")
    public ResponseEntity<Void> updateMemberInformation(@MemberId Long memberId,
                                                        @Valid @RequestBody MemberUpdateRequest request) {
        memberService.updateMemberInformation(request.toCommand(memberId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/reservations")
    public ResponseEntity<ReservationsResponse> getMemberReservations(@MemberId Long memberId, @RequestParam(name = "status", required = false) ReservationStatus status) {
        List<ReservationResult> memberReservations = memberService.getMemberReservations(memberId, status);
        return ResponseEntity.ok(ReservationsResponse.from(memberReservations));
    }

    @GetMapping("/me/reservations/{reservationId}/class-card")
    public ResponseEntity<ReservationResponse> getMemberReservationsClassCard(@MemberId Long memberId, @PathVariable Long reservationId) {
        ReservationResult memberReservations = memberService.getMemberReservation(reservationId);
        return ResponseEntity.ok(ReservationResponse.from(memberReservations));
    }

    @GetMapping("/me/reservations/{reservationId}")
    public ResponseEntity<ReservationDetailedResponse> getReservation(@MemberId Long memberId, @PathVariable Long reservationId) {
        return ResponseEntity.ok(memberService.getMemberReservationDetailed(memberId, reservationId));
    }

    @GetMapping("/me/reservations/status")
    public ResponseEntity<ReservationStatusCountResponses> getMemberReservationsStatusCount(@MemberId Long memberId) {
        return ResponseEntity.ok(memberService.getMemberReservationsStatusCount(memberId));
    }

    @PostMapping("/me/reservations/{reservationId}/cancel")
    public ResponseEntity<Void> cancelReservation(@MemberId Long memberId, @PathVariable Long reservationId, @Valid @RequestBody ReservationCancelRequest request) {
        memberService.cancelMemberReservation(memberId, reservationId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me/reservations/statistics")
    public ResponseEntity<ReservationStatisticsResponse> getReservationStatistics(@MemberId Long memberId) {
        return ResponseEntity.ok(memberService.getReservationStatistics(memberId));
    }
}
