package be.dash.dashserver.api.core.account;

import java.util.List;
import org.springframework.http.ResponseEntity;
import be.dash.dashserver.core.domain.account.Bank;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Bank", description = "Bank API")
public interface BankControllerDocs {

	@Operation(summary = "은행 목록 조회", description = """
	전체 은행 목록을 조회합니다.

	<발생 가능한 케이스>
	(1) 내부 서버 오류
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = Bank.class)))
	})
	ResponseEntity<List<Bank>> findAll();
}


