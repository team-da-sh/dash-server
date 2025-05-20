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
import org.springframework.web.bind.annotation.RestController;
import be.dash.dashserver.api.core.member.dto.MemberResponse;
import be.dash.dashserver.api.core.member.dto.MemberUpdateRequest;
import be.dash.dashserver.api.core.member.dto.MyLessonsResponse;
import be.dash.dashserver.api.core.member.dto.MyLessonsThumbnailResponse;
import be.dash.dashserver.api.core.member.dto.OnBoardRequest;
import be.dash.dashserver.api.core.member.dto.ReservationDetailedResponse;
import be.dash.dashserver.api.core.member.dto.ReservationStatisticsResponse;
import be.dash.dashserver.api.core.member.dto.ReservationsResponse;
import be.dash.dashserver.api.support.MemberId;
import be.dash.dashserver.api.support.Permission;
import be.dash.dashserver.core.domain.member.Role;
import be.dash.dashserver.core.domain.member.service.MemberService;
import be.dash.dashserver.core.domain.member.service.ReservationResult;
import be.dash.dashserver.core.log.annotation.Trace;
import lombok.RequiredArgsConstructor;

@Trace
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;
    private final MemberFacade memberFacade;

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
    public ResponseEntity<ReservationsResponse> getMemberReservations(@MemberId Long memberId) {
        List<ReservationResult> memberReservations = memberService.getMemberReservations(memberId);
        return ResponseEntity.ok(ReservationsResponse.from(memberReservations));
    }

    @GetMapping("/me/reservations/{reservationId}")
    public ResponseEntity<ReservationDetailedResponse> getReservation(@MemberId Long memberId, @PathVariable Long reservationId) {
        return ResponseEntity.ok(memberFacade.getMemberReservation(memberId, reservationId));
    }

    @Permission(role = Role.TEACHER)
    @GetMapping("/me/lessons")
    public ResponseEntity<MyLessonsResponse> getMyLessons(@MemberId Long memberId) {
        return ResponseEntity.ok(memberFacade.getMyLessons(memberId));
    }

    @Permission(role = Role.TEACHER)
    @GetMapping("/me/lessons/thumbnails")
    public ResponseEntity<MyLessonsThumbnailResponse> getMemberLessons(@MemberId Long memberId) {
        return ResponseEntity.ok(memberFacade.getMyLessonsThumbnail(memberId));
    }

    @Permission(role = Role.TEACHER)
    @GetMapping("/me/lessons/{lessonId}")
    public ResponseEntity<MyLessonDetailedResponse> getMemberLesson(@MemberId Long memberId, @PathVariable Long lessonId) {
        return ResponseEntity.ok(memberFacade.getMyLesson(memberId, lessonId));
    }

    @GetMapping("/me/reservations/statistics")
    public ResponseEntity<ReservationStatisticsResponse> getReservationStatistics(@MemberId Long memberId) {
        return ResponseEntity.ok(memberFacade.getReservationStatistics(memberId));
    }

}
