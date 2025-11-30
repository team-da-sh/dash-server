package be.dash.dashserver.api.core.advertisement;

import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import be.dash.dashserver.api.core.advertisement.dto.AdvertisementResponse;
import be.dash.dashserver.api.core.advertisement.dto.AdvertisementResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Advertisement", description = "광고 API")
public interface AdvertisementControllerDocs {

	@Operation(summary = "광고 목록 조회", description = """
	노출 가능한 광고 목록을 조회합니다.

	<발생 가능한 케이스>
	(1) 등록된 광고가 없음
	(2) 내부 서버 오류
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = AdvertisementResponses.class)))
	})
	ResponseEntity<AdvertisementResponses> advertisement();

	@Operation(summary = "광고 상세 조회", description = """
	특정 ID를 가진 광고를 조회합니다.

	<발생 가능한 케이스>
	(1) 존재하지 않는 광고 ID
	(2) 잘못된 식별자 형식
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = AdvertisementResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 식별자 형식"),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 광고")
	})
	ResponseEntity<AdvertisementResponse> findById(
		@Parameter(description = "광고 ID", example = "1", required = true)
		@PathVariable @Min(value = 1L, message = "광고 식별자는 양수로 이루어져야 합니다.") Long id);
}

