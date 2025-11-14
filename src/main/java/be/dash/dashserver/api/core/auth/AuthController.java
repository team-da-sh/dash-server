package be.dash.dashserver.api.core.auth;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import be.dash.dashserver.api.core.auth.dto.LoginRequest;
import be.dash.dashserver.api.core.auth.dto.LoginResponse;
import be.dash.dashserver.api.core.auth.dto.PhoneVerificationApprovalRequest;
import be.dash.dashserver.api.core.auth.dto.PhoneVerificationRequest;
import be.dash.dashserver.api.core.auth.dto.PhoneVerificationResponse;
import be.dash.dashserver.api.core.auth.dto.ReissueResponse;
import be.dash.dashserver.api.core.auth.dto.RoleResponse;
import be.dash.dashserver.api.core.auth.dto.WithdrawResponse;
import be.dash.dashserver.api.support.MemberId;
import be.dash.dashserver.api.support.Permission;
import be.dash.dashserver.core.auth.LoginService;
import be.dash.dashserver.core.auth.LogoutService;
import be.dash.dashserver.core.auth.ReissueService;
import be.dash.dashserver.core.auth.Token;
import be.dash.dashserver.core.auth.TokenService;
import be.dash.dashserver.core.auth.VerificationService;
import be.dash.dashserver.core.auth.WithdrawService;
import be.dash.dashserver.core.auth.command.LoginCommand;
import be.dash.dashserver.core.auth.command.PhoneVerificationApprovalCommand;
import be.dash.dashserver.core.auth.command.PhoneVerificationCommand;
import be.dash.dashserver.core.auth.dto.LoginResult;
import be.dash.dashserver.core.domain.member.Role;
import be.dash.dashserver.core.log.annotation.Trace;
import lombok.RequiredArgsConstructor;

@Trace
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {

    private final LoginService loginService;
    private final ReissueService reissueService;
    private final LogoutService logoutService;
    private final WithdrawService withdrawService;
    private final TokenService tokenService;
    private final VerificationService verificationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResult result = loginService.login(new LoginCommand(request.provider(), request.redirectUrl(), request.code()));
        return ResponseEntity.ok(LoginResponse.of(result));
    }

    @Permission(role = {Role.MEMBER, Role.TEACHER})
    @PostMapping("/reissue")
    public ResponseEntity<ReissueResponse> reissue(@RequestHeader(HttpHeaders.AUTHORIZATION) String refreshToken) {
        Token token = reissueService.reissue(refreshToken);
        return ResponseEntity.ok(ReissueResponse.of(token));
    }

    @Permission(role = {Role.MEMBER, Role.TEACHER})
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@MemberId Long memberId) {
        logoutService.logout(memberId);
        return ResponseEntity.noContent().build();
    }

    @Permission(role = {Role.MEMBER, Role.TEACHER})
    @PostMapping("/withdraw")
    public ResponseEntity<WithdrawResponse> withdraw(@MemberId Long memberId, @RequestHeader(HttpHeaders.AUTHORIZATION) String refreshToken) {

        return ResponseEntity.ok(new WithdrawResponse(withdrawService.withdraw(memberId, tokenService.getRole(refreshToken))));
    }

    @Permission(role = {Role.MEMBER, Role.TEACHER})
    @PostMapping("/validate-withdraw")
    public ResponseEntity<Void> validateWithdrawal(@MemberId Long memberId, @RequestHeader(HttpHeaders.AUTHORIZATION) String refreshToken) {
        withdrawService.validateWithdrawal(memberId, tokenService.getRole(refreshToken));
        return ResponseEntity.ok().build();
    }

    @Permission(role = {Role.MEMBER, Role.TEACHER})
    @PostMapping("/role")
    public ResponseEntity<RoleResponse> role(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {
        return ResponseEntity.ok(new RoleResponse(tokenService.getRole(accessToken)));
    }

    @Permission(role = {Role.MEMBER, Role.TEACHER})
    @PostMapping("/phone/request")
    public ResponseEntity<Void> requestPhoneVerification(@MemberId Long memberId, @RequestBody @Valid PhoneVerificationRequest request) {
        verificationService.requestPhoneVerification(PhoneVerificationCommand.of(memberId, request.phoneNumber()));
        return ResponseEntity.noContent().build();
    }

    @Permission(role = {Role.MEMBER, Role.TEACHER})
    @PostMapping("/phone/verify")
    public ResponseEntity<PhoneVerificationResponse> verifyPhone(@MemberId Long memberId, @RequestBody @Valid PhoneVerificationApprovalRequest request) {
        return ResponseEntity.ok(
                new PhoneVerificationResponse(verificationService.verifyPhone(PhoneVerificationApprovalCommand.of(memberId, request.phoneNumber(), request.code())))
        );
    }
}
