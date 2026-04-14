package be.dash.dashserver.api.core.lesson;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import be.dash.dashserver.api.core.lesson.dto.CreateLessonRequest;
import be.dash.dashserver.api.core.lesson.dto.LessonCreateResponse;
import be.dash.dashserver.api.core.lesson.dto.LessonDetailResponse;
import be.dash.dashserver.api.core.lesson.dto.LessonFilterRequest;
import be.dash.dashserver.api.core.lesson.dto.LessonReservationResponse;
import be.dash.dashserver.api.core.lesson.dto.LessonResponses;
import be.dash.dashserver.api.core.lesson.dto.PaymentRequest;
import be.dash.dashserver.api.core.lesson.dto.PopularGenres;
import be.dash.dashserver.api.core.lesson.dto.UpdateLessonRequest;
import be.dash.dashserver.api.support.MemberId;
import be.dash.dashserver.api.support.OptionalMemberId;
import be.dash.dashserver.core.domain.common.Keyword;
import be.dash.dashserver.core.domain.lesson.LessonSortOption;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Lesson", description = "Lesson API")
public interface LessonControllerDocs {

	@Operation(summary = "수업 검색", description = """
	조건에 맞는 수업을 검색합니다.

	<발생 가능한 케이스>
	(1) 지원하지 않는 정렬 옵션
	(2) 날짜 범위가 올바르지 않음(시작>끝)
	(3) 지원하지 않는 장르/레벨 값
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = LessonResponses.class)))
	})
	ResponseEntity<LessonResponses> search(
		@Parameter(description = "검색 필터") @ModelAttribute LessonFilterRequest lessonFilterRequest,
		@Parameter(description = "정렬 옵션", example = "LATEST") @RequestParam(required = false, defaultValue = "LATEST", name = "sortOption") LessonSortOption sortOption,
		@Parameter(description = "검색 키워드", example = "HIPHOP") @RequestParam(required = false, defaultValue = Keyword.ANY, name = "keyword") Keyword keyword);

	@Operation(summary = "수업 생성", description = """
	댄서가 수업을 생성합니다.

	<발생 가능한 케이스>
	(1) 필수 값 누락(제목, 일정 등)
	(2) 형식 오류(문자 수 제한, 날짜 형식 등)
	(3) 권한 없음(댄서가 아님)
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "생성 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값 검증 실패"),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
    ResponseEntity<LessonCreateResponse> create(
		@Parameter(hidden = true) @MemberId Long memberId,
		@Parameter(description = "수업 생성 요청 바디", required = true) @Valid @RequestBody CreateLessonRequest request);

	@Operation(summary = "최신 수업 조회", description = """
	최근에 등록된 수업 목록을 조회합니다.
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = LessonResponses.class)))
	})
	ResponseEntity<LessonResponses> latest();

	@Operation(summary = "인기 장르 조회", description = """
	인기 장르를 집계하여 반환합니다.
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = PopularGenres.class)))
	})
	ResponseEntity<PopularGenres> popularGenres();

	@Operation(summary = "다가오는 수업 조회", description = """
	진행 예정인 수업을 조회합니다.
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = LessonResponses.class)))
	})
	ResponseEntity<LessonResponses> upcoming();

	@Operation(summary = "수업 상세 조회", description = """
	수업 상세 정보를 조회합니다. 비로그인(GUEST)도 조회할 수 있습니다.
	Authorization 헤더가 있으면 예약 여부(bookStatus) 등 개인화 정보가 포함됩니다.

	<발생 가능한 케이스>
	(1) 존재하지 않는 수업 식별자
	(2) 잘못된 식별자 형식
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = LessonDetailResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 식별자"),
		@ApiResponse(responseCode = "401", description = "잘못되었거나 만료된 토큰(선택 인증 헤더를 보낸 경우)"),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 수업")
	})
	ResponseEntity<LessonDetailResponse> findById(
		@Parameter(hidden = true) @OptionalMemberId Long memberId,
		@Parameter(description = "수업 ID", example = "1", required = true)
		@PathVariable @Min(value = 1L, message = "수업의 식별자는 양수로 이루어져야 합니다.") long lessonId);

	@Operation(summary = "수업 예약 진행 정보 조회", description = """
	예약(클래스 신청) 진행에 필요한 정보를 조회합니다. 로그인(회원·강사)이 필요합니다.

	<발생 가능한 케이스>
	(1) 존재하지 않는 수업
	(2) 인증 실패·권한 없음
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = LessonReservationResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	ResponseEntity<LessonReservationResponse> reserveProgress(
		@Parameter(hidden = true) @MemberId Long memberId,
		@Parameter(description = "수업 ID", example = "1", required = true)
		@PathVariable @Min(value = 1L, message = "수업의 식별자는 양수로 이루어져야 합니다.") long lessonId);

	@Operation(summary = "수업 예약 생성", description = """
	수업 예약을 생성합니다.

	<발생 가능한 케이스>
	(1) 결제/요청 값 검증 실패
	(2) 마감된 수업 또는 좌석 부족
	(3) 권한 없음
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "예약 생성 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값 검증 실패"),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	ResponseEntity<Void> createReservation(
		@Parameter(hidden = true) @MemberId Long memberId,
		@Parameter(description = "결제 요청 바디", required = true) @Valid @RequestBody PaymentRequest paymentRequest,
		@Parameter(description = "수업 ID", example = "1", required = true)
		@PathVariable @Min(value = 1L, message = "수업의 식별자는 양수로 이루어져야 합니다.") long lessonId);

	@Operation(
			summary = "수업 수정",
			description = """
        댄서가 수업을 수정합니다.
        <발생 가능한 케이스>
        (1) 필수 값 누락(제목, 일정 등)
        (2) 형식 오류(문자 수 제한, 날짜 형식 등)
        (3) 권한 없음(댄서가 아님)
        """
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "수업 수정 성공"),
			@ApiResponse(responseCode = "400", description = "요청 값 검증 실패"),
			@ApiResponse(responseCode = "401", description = "인증 실패"),
			@ApiResponse(responseCode = "403", description = "권한 없음")
	})
	ResponseEntity<Void> update(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(
					required = true,
					description = "수업 수정 요청 바디",
					content = @Content(schema = @Schema(implementation = UpdateLessonRequest.class))
			)
			@Valid @RequestBody UpdateLessonRequest request,
			@Parameter(description = "수업 ID", example = "1", required = true)
			@PathVariable Long lessonId
	);
}


