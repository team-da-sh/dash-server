package be.dash.dashserver.api.core.member;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
import be.dash.dashserver.core.domain.reservation.ReservationStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Member", description = "Member API")
public interface MemberControllerDocs {

	@Operation(summary = "온보딩", description = """
	회원 온보딩을 진행합니다.

	<발생 가능한 케이스>
	(1) 필수 값 누락
	(2) 형식 오류(이름, 닉네임 등)
	(3) 중복 정보 존재
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "온보딩 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값 검증 실패"),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	ResponseEntity<Void> onboard(
		@Parameter(hidden = true) @MemberId Long memberId,
		@Parameter(description = "온보딩 요청 바디", required = true)
		@Valid @RequestBody OnBoardRequest request);

	@Operation(summary = "내 정보 조회", description = """
	현재 로그인한 회원 정보를 조회합니다.

	<발생 가능한 케이스>
	(1) 인증 정보 누락/만료
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = MemberResponse.class)))
	})
	ResponseEntity<MemberResponse> getMemberInformation(
		@Parameter(hidden = true) @MemberId Long memberId);

	@Operation(summary = "내 정보 수정", description = """
	회원 정보를 수정합니다.

	<발생 가능한 케이스>
	(1) 필수 값 누락/형식 오류
	(2) 닉네임 중복
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "수정 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값 검증 실패")
	})
	ResponseEntity<Void> updateMemberInformation(
		@Parameter(hidden = true) @MemberId Long memberId,
		@Parameter(description = "회원 정보 수정 요청 바디", required = true)
		@Valid @RequestBody MemberUpdateRequest request);

	@Operation(summary = "내 예약 목록 조회", description = """
	회원의 예약 목록을 조회합니다.

	<발생 가능한 케이스>
	(1) 지원하지 않는 상태 값
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = ReservationsResponse.class)))
	})
	ResponseEntity<ReservationsResponse> getMemberReservations(
		@Parameter(hidden = true) @MemberId Long memberId,
		@Parameter(description = "예약 상태 필터")
		@RequestParam(name = "status", required = false) ReservationStatus status);

	@Operation(summary = "내 예약 클래스 카드 조회", description = """
	예약에 대한 클래스 카드 정보를 조회합니다.

	<발생 가능한 케이스>
	(1) 존재하지 않는 예약 ID
	(2) 권한 없음
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = ReservationResponse.class)))
	})
	ResponseEntity<ReservationResponse> getMemberReservationsClassCard(
		@Parameter(hidden = true) @MemberId Long memberId,
		@Parameter(description = "예약 ID", example = "100") @PathVariable Long reservationId);

	@Operation(summary = "예약 상세 조회", description = """
	예약 상세 정보를 조회합니다.

	<발생 가능한 케이스>
	(1) 존재하지 않는 예약 ID
	(2) 권한 없음
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = ReservationDetailedResponse.class)))
	})
	ResponseEntity<ReservationDetailedResponse> getReservation(
		@Parameter(hidden = true) @MemberId Long memberId,
		@Parameter(description = "예약 ID", example = "100") @PathVariable Long reservationId);

	@Operation(summary = "예약 상태 집계 조회", description = """
	회원 예약 상태를 집계하여 반환합니다.

	<발생 가능한 케이스>
	(1) 인증 정보 누락/만료
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = ReservationStatusCountResponses.class)))
	})
	ResponseEntity<ReservationStatusCountResponses> getMemberReservationsStatusCount(
		@Parameter(hidden = true) @MemberId Long memberId);

	@Operation(summary = "예약 취소", description = """
	회원 예약을 취소합니다.

	<발생 가능한 케이스>
	(1) 존재하지 않는 예약 ID
	(2) 취소 불가 상태
	(3) 권한 없음
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "취소 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값 검증 실패")
	})
	ResponseEntity<Void> cancelReservation(
		@Parameter(hidden = true) @MemberId Long memberId,
		@Parameter(description = "예약 ID", example = "100") @PathVariable Long reservationId,
		@Parameter(description = "예약 취소 요청 바디", required = true)
		@Valid @RequestBody ReservationCancelRequest request);

	@Operation(summary = "예약 통계 조회", description = """
	회원의 예약 통계를 조회합니다.

	<발생 가능한 케이스>
	(1) 인증 정보 누락/만료
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = ReservationStatisticsResponse.class)))
	})
	ResponseEntity<ReservationStatisticsResponse> getReservationStatistics(
		@Parameter(hidden = true) @MemberId Long memberId);
}


