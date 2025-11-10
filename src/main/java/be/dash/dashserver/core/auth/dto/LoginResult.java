package be.dash.dashserver.core.auth.dto;

import be.dash.dashserver.core.auth.Token;

public record LoginResult(
        String accessToken,
        String refreshToken,
        boolean isOnboarded,
        boolean isDeleted
) {
    public static LoginResult of(Token token, boolean isOnboarded, boolean isDeleted) {
        return new LoginResult(token.accessToken(), token.refreshToken(), isOnboarded, isDeleted);
    }
}
