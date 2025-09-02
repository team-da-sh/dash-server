package be.dash.dashserver.core.auth;

public interface PhoneVerificationRepository {
    void saveCode(long memberId, String phoneNumber, String code);

    String getCode(long memberId, String phoneNumber);

    void removeCode(long memberId, String phoneNumber);
}
