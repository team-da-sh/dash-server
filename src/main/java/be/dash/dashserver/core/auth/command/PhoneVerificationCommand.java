package be.dash.dashserver.core.auth.command;

public record PhoneVerificationCommand(long memberId, String phoneNumber) {
    public static PhoneVerificationCommand of(long memberId, String phoneNumber) {
        return new PhoneVerificationCommand(memberId, phoneNumber);
    }
}
