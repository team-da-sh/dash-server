package be.dash.dashserver.api.core.external;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import be.dash.dashserver.api.core.external.dto.LocationsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "External - Location", description = "외부 위치 검색 API")
public interface LocationSearchControllerDocs {

	@Operation(summary = "위치 검색", description = """
	쿼리 문자열로 위치를 검색합니다.

	<발생 가능한 케이스>
	(1) 빈 검색어
	(2) 외부 위치 API 오류
	(3) 결과 없음
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = LocationsResponse.class)))
	})
	ResponseEntity<LocationsResponse> getLocations(
		@Parameter(description = "검색어", example = "강남역", required = true)
		@RequestParam @NotBlank String query);
}


