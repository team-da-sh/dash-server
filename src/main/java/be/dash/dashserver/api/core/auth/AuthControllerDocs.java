package be.dash.dashserver.api.core.auth;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import be.dash.dashserver.api.core.auth.dto.LoginRequest;
import be.dash.dashserver.api.core.auth.dto.LoginResponse;
import be.dash.dashserver.api.core.auth.dto.PhoneVerificationApprovalRequest;
import be.dash.dashserver.api.core.auth.dto.PhoneVerificationRequest;
import be.dash.dashserver.api.core.auth.dto.PhoneVerificationResponse;
import be.dash.dashserver.api.core.auth.dto.ReissueResponse;
import be.dash.dashserver.api.core.auth.dto.RoleResponse;
import be.dash.dashserver.api.support.MemberId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth", description = "Authentication API")
public interface AuthControllerDocs {

	@Operation(summary = "로그인", description = """
	소셜 로그인 후 토큰을 발급합니다.

	<발생 가능한 케이스>
	(1) 필수 값 누락(provider, code)
	(2) 잘못된 provider 또는 만료된 code
	(3) 인증 서버 통신 오류
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "로그인 성공",
			content = @Content(schema = @Schema(implementation = LoginResponse.class))),
		@ApiResponse(responseCode = "400", description = "요청 값 검증 실패"),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	ResponseEntity<LoginResponse> login(
		@Parameter(description = "로그인 요청 바디", required = true)
		@RequestBody LoginRequest request);

	@Operation(summary = "토큰 재발급", description = """
	리프레시 토큰으로 액세스 토큰을 재발급합니다.

	<발생 가능한 케이스>
	(1) Authorization 헤더 누락
	(2) 만료되었거나 위조된 리프레시 토큰
	(3) 블랙리스트(로그아웃) 처리된 토큰
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "재발급 성공",
			content = @Content(schema = @Schema(implementation = ReissueResponse.class))),
		@ApiResponse(responseCode = "401", description = "유효하지 않은 토큰")
	})
	ResponseEntity<ReissueResponse> reissue(
		@Parameter(description = "Authorization 헤더에 담긴 Refresh 토큰", example = "Bearer eyJ...", required = true)
		@RequestHeader(HttpHeaders.AUTHORIZATION) String refreshToken);

	@Operation(summary = "로그아웃", description = """
	현재 사용자를 로그아웃합니다.

	<발생 가능한 케이스>
	(1) 인증 정보 누락
	(2) 이미 로그아웃된 토큰
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "로그아웃 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	ResponseEntity<Void> logout(
		@Parameter(hidden = true) @MemberId Long memberId);

	@Operation(summary = "역할 조회", description = """
	액세스 토큰으로 사용자 역할을 조회합니다.

	<발생 가능한 케이스>
	(1) Authorization 헤더 누락
	(2) 만료되었거나 위조된 액세스 토큰
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "역할 조회 성공",
			content = @Content(schema = @Schema(implementation = RoleResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	ResponseEntity<RoleResponse> role(
		@Parameter(description = "Authorization 헤더에 담긴 Access 토큰", example = "Bearer eyJ...", required = true)
		@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken);

	@Operation(summary = "휴대폰 인증 요청", description = """
	휴대폰 인증 코드를 요청합니다.

	<발생 가능한 케이스>
	(1) 전화번호 형식 오류
	(2) 과도한 요청(레이트 리밋)
	(3) SMS 게이트웨이 오류
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "요청 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값 검증 실패"),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	ResponseEntity<Void> requestPhoneVerification(
		@Parameter(hidden = true) @MemberId Long memberId,
		@Parameter(description = "휴대폰 인증 요청 바디", required = true)
		@RequestBody @Valid PhoneVerificationRequest request);

	@Operation(summary = "휴대폰 인증 확인", description = """
	휴대폰 인증 코드를 검증합니다.

	<발생 가능한 케이스>
	(1) 잘못된 인증 코드
	(2) 만료된 인증 코드
	(3) 요청/확인 전화번호 불일치
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "검증 성공",
			content = @Content(schema = @Schema(implementation = PhoneVerificationResponse.class))),
		@ApiResponse(responseCode = "400", description = "요청 값 검증 실패"),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	ResponseEntity<PhoneVerificationResponse> verifyPhone(
		@Parameter(hidden = true) @MemberId Long memberId,
		@Parameter(description = "휴대폰 인증 확인 바디", required = true)
		@RequestBody @Valid PhoneVerificationApprovalRequest request);

	@Operation(
			summary = "회원 탈퇴",
			description = """
        회원 탈퇴를 진행합니다.

        <발생 가능한 케이스>
        (1) 인증 정보 누락
        (2) 이미 탈퇴된 회원
        (3) 진행중인 수업 또는 예약 존재
        """
			// 필요 시 인증 스키마 쓰면 추가
			// , security = { @SecurityRequirement(name = "bearerAuth") }
	)
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "탈퇴 성공"),
			@ApiResponse(responseCode = "401", description = "인증 실패"),
			@ApiResponse(responseCode = "400", description = "탈퇴 불가 상태")
	})
	ResponseEntity<Void> withdraw(
			@Parameter(hidden = true) @MemberId Long memberId,
			@Parameter(
					name = HttpHeaders.AUTHORIZATION,
					description = "Refresh 토큰 (예: Bearer {token})",
					required = true
			)
			@RequestHeader(HttpHeaders.AUTHORIZATION) String refreshToken
	);
}


