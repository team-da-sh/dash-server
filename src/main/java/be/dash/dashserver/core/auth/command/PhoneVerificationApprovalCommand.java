package be.dash.dashserver.core.auth.command;

public record PhoneVerificationApprovalCommand(long memberId,
                                               String phoneNumber,
                                               String code) {
    public static PhoneVerificationApprovalCommand of(long memberId, String phoneNumber, String code) {
        return new PhoneVerificationApprovalCommand(memberId, phoneNumber, code);
    }
}
