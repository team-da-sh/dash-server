package be.dash.dashserver.api.core.external;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import be.dash.dashserver.api.core.external.dto.ImagePostResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "External - Image", description = "이미지 업로드 API")
public interface ImageControllerDocs {

	@Operation(summary = "이미지 업로드", description = """
	이미지를 업로드하고 URL을 반환합니다.

	<발생 가능한 케이스>
	(1) 파일 누락
	(2) 허용되지 않는 MIME 타입/확장자
	(3) 파일 크기 초과
	(4) 스토리지 업로드 실패
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "업로드 성공",
			content = @Content(schema = @Schema(implementation = ImagePostResponse.class))),
		@ApiResponse(responseCode = "400", description = "파일 누락 또는 유효하지 않은 파일")
	})
	ResponseEntity<ImagePostResponse> createStore(
		@Parameter(description = "업로드할 이미지 파일", required = true)
		@RequestPart MultipartFile image);
}


