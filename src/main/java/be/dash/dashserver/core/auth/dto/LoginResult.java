package be.dash.dashserver.core.auth.dto;

import be.dash.dashserver.core.auth.Token;
import be.dash.dashserver.core.domain.member.Role;

public record LoginResult(
        String accessToken,
        String refreshToken,
        boolean isOnboarded,
        boolean isDeleted,
        Role role
) {
    public static LoginResult of(Token token, boolean isOnboarded, boolean isDeleted, Role role) {
        return new LoginResult(token.accessToken(), token.refreshToken(), isOnboarded, isDeleted, role);
    }
}
