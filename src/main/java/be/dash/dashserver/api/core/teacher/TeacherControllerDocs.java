package be.dash.dashserver.api.core.teacher;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import be.dash.dashserver.api.core.account.dto.AccountRequest;
import be.dash.dashserver.api.core.member.MyLessonDetailedResponse;
import be.dash.dashserver.api.core.member.dto.ApplyStatus;
import be.dash.dashserver.api.core.member.dto.MyLessonsResponse;
import be.dash.dashserver.api.core.member.dto.MyLessonsThumbnailResponse;
import be.dash.dashserver.api.core.teacher.dto.ChangeApproveStatusResponse;
import be.dash.dashserver.api.core.teacher.dto.CreateTeacherRequest;
import be.dash.dashserver.api.core.teacher.dto.CreateTeacherResponse;
import be.dash.dashserver.api.core.teacher.dto.LessonStatusCountResponses;
import be.dash.dashserver.api.core.teacher.dto.NicknameValidationResponse;
import be.dash.dashserver.api.core.teacher.dto.TeacherAccountResponse;
import be.dash.dashserver.api.core.teacher.dto.TeacherDetailResponse;
import be.dash.dashserver.api.core.teacher.dto.TeacherProfileDetailResponse;
import be.dash.dashserver.api.core.teacher.dto.TeacherProfileResponse;
import be.dash.dashserver.api.core.teacher.dto.TeacherResponses;
import be.dash.dashserver.api.core.teacher.dto.TeacherUpdateRequest;
import be.dash.dashserver.api.support.MemberId;
import be.dash.dashserver.core.domain.common.Keyword;
import be.dash.dashserver.core.domain.reservation.ReservationStatusType;
import be.dash.dashserver.core.domain.teacher.TeacherLessonGenres;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Teacher", description = "Teacher API")
public interface TeacherControllerDocs {

	@Operation(summary = "댄서 검색", description = """
	조건에 맞는 댄서를 검색합니다.

	<발생 가능한 케이스>
	(1) 지원하지 않는 키워드 값
	(2) 내부 검색 엔진 오류
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = TeacherResponses.class)))
	})
	ResponseEntity<TeacherResponses> search(
		@Parameter(description = "검색 키워드 (기본값 ANY)", example = "HIPHOP")
		@RequestParam(required = false, defaultValue = Keyword.ANY, name = "keyword") Keyword keyword);

	@Operation(summary = "댄서 등록", description = """
	회원이 댄서 프로필을 등록합니다.

	<발생 가능한 케이스>
	(1) 필수 값 누락(닉네임 등)
	(2) 닉네임 중복
	(3) 형식 오류(문자 수 제한, 패턴 불일치 등)
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "등록 성공",
			content = @Content(schema = @Schema(implementation = CreateTeacherResponse.class))),
		@ApiResponse(responseCode = "400", description = "요청 값 검증 실패"),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	ResponseEntity<CreateTeacherResponse> create(
		@Parameter(hidden = true) @MemberId Long memberId,
		@Parameter(description = "댄서 등록 요청 바디", required = true)
		@Valid @RequestBody CreateTeacherRequest request);

	@Operation(summary = "댄서 상세 조회", description = """
	댄서 상세 정보를 조회합니다.

	<발생 가능한 케이스>
	(1) 존재하지 않는 댄서 식별자
	(2) 잘못된 식별자 형식
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = TeacherDetailResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 식별자"),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 댄서")
	})
	ResponseEntity<TeacherDetailResponse> find(
		@Parameter(description = "댄서 ID", example = "1", required = true)
		@PathVariable @Min(value = 1L, message = "댄서의 식별자는 양수로 이루어져야 합니다.") long teacherId);

	@Operation(summary = "내 댄서 프로필 조회", description = """
	현재 사용자(댄서)의 프로필을 조회합니다.

	<발생 가능한 케이스>
	(1) 권한 없음(댄서가 아님)
	(2) 프로필 미생성
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = TeacherProfileResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	ResponseEntity<TeacherProfileResponse> findMyTeacherProfile(
		@Parameter(hidden = true) @MemberId Long memberId);

	@Operation(summary = "내 댄서 상세 프로필 조회", description = """
	현재 사용자(댄서)의 상세 프로필을 조회합니다.

	<발생 가능한 케이스>
	(1) 권한 없음
	(2) 상세 프로필 미생성
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = TeacherProfileDetailResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	ResponseEntity<TeacherProfileDetailResponse> findMyTeacherDetail(
		@Parameter(hidden = true) @MemberId Long memberId);

	@Operation(summary = "내 정산 계좌 조회", description = """
	댄서의 정산 계좌 정보를 조회합니다.

	<발생 가능한 케이스>
	(1) 권한 없음
	(2) 계좌 정보 미등록
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = TeacherAccountResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	ResponseEntity<TeacherAccountResponse> findMyTeacherAccount(
		@Parameter(hidden = true) @MemberId Long memberId);

	@Operation(summary = "내 정산 계좌 등록", description = """
	댄서의 정산 계좌 정보를 등록합니다.

	<발생 가능한 케이스>
	(1) 필수 값 누락(은행, 계좌번호 등)
	(2) 잘못된 계좌 형식
	(3) 이미 등록된 계좌
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "등록 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값 검증 실패"),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	ResponseEntity<Void> registerMyTeacherAccount(
		@Parameter(hidden = true) @MemberId Long memberId,
		@Parameter(description = "정산 계좌 등록 요청 바디", required = true)
		@RequestBody @Valid AccountRequest teacherAccountRequest);

	@Operation(summary = "내 프로필 수정", description = """
	댄서 프로필을 수정합니다.

	<발생 가능한 케이스>
	(1) 필수 값 누락/형식 오류
	(2) 닉네임 중복
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "수정 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값 검증 실패"),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	ResponseEntity<Void> updateTeacherProfile(
		@Parameter(hidden = true) @MemberId Long memberId,
		@Parameter(description = "프로필 수정 요청 바디", required = true)
		@Valid @RequestBody TeacherUpdateRequest request);

	@Operation(summary = "내 특정 수업 조회", description = """
	내가 개설한 특정 수업 상세를 조회합니다.

	<발생 가능한 케이스>
	(1) 존재하지 않는 수업
	(2) 접근 권한 없음
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = MyLessonDetailedResponse.class)))
	})
	ResponseEntity<MyLessonDetailedResponse> getMyLesson(
		@Parameter(hidden = true) @MemberId Long memberId,
		@Parameter(description = "수업 ID", example = "10", required = true) @PathVariable Long lessonId,
		@Parameter(description = "예약 상태", example = "APPLY") @RequestParam(name = "status") ReservationStatusType status);

	@Operation(summary = "내 수업 목록 조회", description = """
	내가 개설한 수업 목록을 조회합니다.

	<발생 가능한 케이스>
	(1) 지원하지 않는 상태 필터
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = MyLessonsResponse.class)))
	})
	ResponseEntity<MyLessonsResponse> getMyLessons(
		@Parameter(hidden = true) @MemberId Long memberId,
		@Parameter(description = "신청 상태 필터") @RequestParam(name = "status", required = false) ApplyStatus status);

	@Operation(summary = "내 수업 썸네일 목록 조회", description = """
	내 수업 목록의 썸네일 요약 정보를 조회합니다.

	<발생 가능한 케이스>
	(1) 권한 없음
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = MyLessonsThumbnailResponse.class)))
	})
	ResponseEntity<MyLessonsThumbnailResponse> getMyLessonsThumbnail(
		@Parameter(hidden = true) @MemberId Long memberId);

	@Operation(summary = "내 수업 상태 집계 조회", description = """
	내 수업의 상태 통계를 조회합니다.

	<발생 가능한 케이스>
	(1) 권한 없음
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = LessonStatusCountResponses.class)))
	})
	ResponseEntity<LessonStatusCountResponses> getMyLessonsStatusCount(
		@Parameter(hidden = true) @MemberId Long memberId);

	@Operation(summary = "닉네임 중복 검사", description = """
	닉네임 사용 가능 여부를 검사합니다.

	<발생 가능한 케이스>
	(1) 빈 문자열/형식 오류
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "검사 성공",
			content = @Content(schema = @Schema(implementation = NicknameValidationResponse.class)))
	})
	ResponseEntity<NicknameValidationResponse> validateNickname(
		@Parameter(description = "닉네임", example = "dash_dancer") @RequestParam String nickname);

	@Operation(summary = "수업 승인 상태 변경", description = """
	특정 예약의 승인 상태를 변경합니다.

	<발생 가능한 케이스>
	(1) 존재하지 않는 예약/수업
	(2) 권한 없음
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "변경 성공",
			content = @Content(schema = @Schema(implementation = ChangeApproveStatusResponse.class)))
	})
	ResponseEntity<ChangeApproveStatusResponse> changeApproveStatus(
		@Parameter(hidden = true) @MemberId Long memberId,
		@Parameter(description = "수업 ID", example = "10") @PathVariable Long lessonId,
		@Parameter(description = "예약 ID", example = "100") @PathVariable Long reservationId);

	@Operation(summary = "수업 취소 상태 변경", description = """
	특정 예약의 취소 상태를 변경합니다.

	<발생 가능한 케이스>
	(1) 존재하지 않는 예약/수업
	(2) 권한 없음
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "변경 성공")
	})
	ResponseEntity<Void> changeCancelStatus(
		@Parameter(hidden = true) @MemberId Long memberId,
		@Parameter(description = "수업 ID", example = "10") @PathVariable Long lessonId,
		@Parameter(description = "예약 ID", example = "100") @PathVariable Long reservationId);
}


