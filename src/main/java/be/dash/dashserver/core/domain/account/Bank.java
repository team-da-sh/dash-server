package be.dash.dashserver.core.domain.account;

public record Bank(
        Long bankId,
        String bankImageUrl,
        String bankName
) {
}
