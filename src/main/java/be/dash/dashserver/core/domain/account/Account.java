package be.dash.dashserver.core.domain.account;

public record Account(
        Boolean isRegistered,
        String depositor,
        String accountNumber,
        Long memberId,
        Long bankId,
        Boolean isTeacherAccount,
        String bankImageUrl,
        String bankName
) {
    public static Account empty() {
        return new Account(false, null, null, null, null, null, null, null);
    }
}
