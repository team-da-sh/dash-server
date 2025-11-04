package be.dash.dashserver.api.core.lesson;

import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import be.dash.dashserver.api.core.lesson.dto.LessonAccountResponse;
import be.dash.dashserver.api.core.lesson.dto.LessonAccount;
import be.dash.dashserver.api.support.MemberId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Lesson V2", description = "Lesson API V2")
public interface LessonControllerV2Docs {

	@Operation(summary = "수업 예약 생성(V2)", description = """
	수업 예약을 준비하고 결제 계정 정보를 반환합니다.

	<발생 가능한 케이스>
	(1) 존재하지 않는 수업
	(2) 권한 없음
	(3) 이미 예약됨
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "예약 준비 정보 반환",
			content = @Content(schema = @Schema(implementation = LessonAccountResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	ResponseEntity<LessonAccountResponse> createReservation(
		@Parameter(hidden = true) @MemberId Long memberId,
		@Parameter(description = "수업 ID", example = "1", required = true)
		@PathVariable @Min(value = 1L, message = "수업의 식별자는 양수로 이루어져야 합니다.") long lessonId);
}


