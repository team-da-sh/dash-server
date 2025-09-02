package be.dash.dashserver.core.auth;

public interface PhoneVerificationQuotaRepository {
    boolean tryConsumeDailyQuota(long memberId);
    int getRemainingDailyQuota(long memberId);
    void resetDailyQuota(long l);
}
