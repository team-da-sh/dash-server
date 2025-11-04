package be.dash.dashserver.api.core.advertisement;

import org.springframework.http.ResponseEntity;
import be.dash.dashserver.api.core.advertisement.dto.AdvertisementResponses;
import io.swagger.v3.oas.annotations.Operation;
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
}


