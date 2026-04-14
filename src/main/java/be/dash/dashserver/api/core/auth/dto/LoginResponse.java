package be.dash.dashserver.api.core.auth.dto;

import be.dash.dashserver.core.auth.dto.LoginResult;
import be.dash.dashserver.core.domain.member.Role;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        boolean isOnboarded,
        boolean isDeleted,
        Role role
) {
    public static LoginResponse of(LoginResult result) {
        return new LoginResponse(result.accessToken(), result.refreshToken(), result.isOnboarded(), result.isDeleted(), result.role());
    }
}
